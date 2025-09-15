package ru.otus.exchange.fxml;

import static ru.otus.exchange.fxml.IXmlParser.TOKEN_TYPE.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class CharArrXMLParser implements IXmlParser {

    @Getter
    @Setter
    private Reader reader;

    private final int charBuffSize;

    private final IBuffer buffer;

    List<char[]> tagInternalList;

    private int tagInternalPosition;

    private int tagInternalFullSize;
    private boolean eofReached;
    private static final String UTF_8_ENCODING = "UTF-8";

    protected void clearTagInternalList() {
        tagInternalPosition = 0;
        tagInternalFullSize = 0;
        tagInternalList.clear();
        int blockSize = 64;
        char[] first = new char[blockSize];
        tagInternalList.add(first);
    }

    public char[] getTagInternal() {
        char[] fullBlock = new char[tagInternalFullSize];
        int fromPosition = 0;
        for (int i = 0; i < tagInternalList.size(); i++) {
            char[] block = tagInternalList.get(i);
            int blockLengt = i == tagInternalList.size() - 1 ? tagInternalPosition : block.length;
            System.arraycopy(block, 0, fullBlock, fromPosition, blockLengt);
            fromPosition += blockLengt;
        }
        return fullBlock;
    }

    protected void writeToTagInternal(String str) throws IOException {
        writeToTagInternal(str.toCharArray());
    }

    protected void writeToTagInternal(char[] charSeq) throws IOException {
        char[] currentBlock = tagInternalList.getLast();
        int charSecPos = 0;
        int charSecToWrite = charSeq.length;

        while (currentBlock.length - tagInternalPosition < charSecToWrite) {
            System.arraycopy(
                    charSeq, charSecPos, currentBlock, tagInternalPosition, currentBlock.length - tagInternalPosition);
            charSecToWrite -= currentBlock.length - tagInternalPosition;
            charSecPos += currentBlock.length - tagInternalPosition;

            tagInternalFullSize += currentBlock.length - tagInternalPosition;

            currentBlock = new char[currentBlock.length * 2];
            tagInternalList.add(currentBlock);
            tagInternalPosition = 0;
        }

        System.arraycopy(charSeq, charSecPos, currentBlock, tagInternalPosition, charSecToWrite);
        tagInternalFullSize += charSecToWrite;
        tagInternalPosition += charSecToWrite;
        buffer.addToCurrentBufferPosition(charSeq.length);
        shiftIfNecessary();
    }

    protected void writeToTagInternal(char chr) throws IOException {
        writeToTagInternal(new char[] {chr});
    }

    public static String getEncoding(ByteArrayInputStream inputStream) throws IOException {
        inputStream.mark(0);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String encoding = UTF_8_ENCODING;

        // XMLDecl	   ::=   	'<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
        if (reader.read() != '<'
                || reader.read() != '?'
                || reader.read() != 'x'
                || reader.read() != 'm'
                || reader.read() != 'l') {
            inputStream.reset();
            return encoding;
        }

        int ch;
        boolean stop = false;
        boolean isEncodingFound = false;
        while ((ch = reader.read()) != -1 && ch != '?' && !stop) {
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1 && isNameChar(ch)) {
                sb.append((char) ch);
            }

            if (isEncodingFound && !sb.toString().equalsIgnoreCase("")) {
                encoding = sb.toString().equals(encoding) ? UTF_8_ENCODING : sb.toString();
                stop = true;
            } else if (sb.toString().equalsIgnoreCase("encoding")) {
                isEncodingFound = true;
            }
        }
        inputStream.reset();
        return encoding;
    }

    public CharArrXMLParser(ByteArrayInputStream inputStream) throws IOException {
        this(inputStream, new SimpleBuffer());
    }

    public CharArrXMLParser(InputStream inputStream, IBuffer buffer) throws IOException {

        this.reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8), IBuffer.CHARACTER_ARRAY_BUFFER_SIZE);

        this.buffer = buffer;
        charBuffSize = IBuffer.CHARACTER_ARRAY_BUFFER_SIZE;
        buffer.setMainBuffer(new char[charBuffSize]);
        int read = reader.read(buffer.getMainBuffer());
        buffer.setMainBufferRealLen(read);
        if (read != charBuffSize) {
            eofReached = true;
        }
        buffer.setCurrentBufferPosition(0);
        buffer.setFilePosition(1); // start with 1
        tagInternalList = new LinkedList<>();
        tagInternalPosition = 0;
        tagInternalFullSize = 0;
        int blockSize = 64;
        char[] first = new char[blockSize];
        tagInternalList.add(first);
    }

    protected void shiftIfNecessary() throws IOException {
        if (buffer.getMainBufferRealLen() == charBuffSize
                && buffer.getCurrentBufferPosition() >= charBuffSize / 2
                && !eofReached) {
            char[] newBuffer = new char[charBuffSize];
            System.arraycopy(buffer.getMainBuffer(), charBuffSize / 2, newBuffer, 0, charBuffSize / 2);
            int readed = reader.read(newBuffer, charBuffSize / 2, charBuffSize / 2);
            buffer.setMainBuffer(newBuffer);
            if (readed > 0) {
                buffer.setMainBufferRealLen(charBuffSize / 2 + readed);
            } else {
                buffer.setMainBufferRealLen(charBuffSize / 2);
            }
            if (readed != charBuffSize / 2) {
                eofReached = true;
            }
            buffer.incBufferPositionCounter();
            buffer.decCurrentBufferPosition(charBuffSize / 2);
        }
    }

    @SuppressWarnings("java:S1119")
    protected String getOneOfThem(String... args) throws IOException {
        shiftIfNecessary();
        label:
        for (String arg : args) {
            int len = arg.length();

            if (buffer.getCurrentBufferPosition() + len > buffer.getMainBuffer().length) {
                if (eofReached) {
                    continue;
                } else {
                    throw new IOException("xml is not complete");
                }
            }
            char[] argArr = arg.toCharArray();
            char[] mainBuffer = buffer.getMainBuffer();

            String bufferStr = String.valueOf(buffer.getMainBuffer(), buffer.getCurrentBufferPosition(), len);
            int pos = buffer.getCurrentBufferPosition();

            for (int i = 0; i < len; ++i) {
                if (mainBuffer[pos + i] != argArr[i]) {
                    continue label;
                }
            }
            buffer.addToFilePosition(len);
            writeToTagInternal(bufferStr);
            return arg;
        }
        return null;
    }

    protected int getOneOfThem(char... args) throws IOException {
        if (!isEnd()) {
            shiftIfNecessary();
            for (char arg : args) {
                if (buffer.getMainBuffer()[buffer.getCurrentBufferPosition()] == arg) {
                    buffer.incFilePosition();
                    writeToTagInternal(arg);
                    return arg;
                }
            }
        }
        return -1;
    }

    protected boolean isEnd() {
        return buffer.getMainBufferRealLen() == buffer.getCurrentBufferPosition() && eofReached;
    }

    @SuppressWarnings("java:S3776")
    protected void readUntilOccur(String headSearchAttr, String... tailSearchAttr) throws IOException {

        while (!isEnd()) {
            String str = "";
            try {
                str = String.valueOf(
                        buffer.getMainBuffer(),
                        buffer.getCurrentBufferPosition(),
                        buffer.getMainBufferRealLen() - buffer.getCurrentBufferPosition());
            } catch (StringIndexOutOfBoundsException ex) {
                throw new IOException("xml is not complete");
            }
            int index = str.indexOf(headSearchAttr);

            for (String arg : tailSearchAttr) {
                int spos = str.indexOf(arg);
                if (spos > 0 && spos < index) index = spos;
            }

            if (index > -1 && index != buffer.getMainBufferRealLen()) {
                char[] result = new char[index];
                System.arraycopy(buffer.getMainBuffer(), buffer.getCurrentBufferPosition(), result, 0, index);
                writeToTagInternal(result);

                for (int i = 0; i < index; i++) {
                    int chr = result[i];
                    if ((chr >= 1040 && chr <= 1103) || chr == 1105 || chr == 1025) { // коды кириллицы
                        buffer.incCyrillicSymbols();
                    }
                }

                buffer.addToFilePosition(index);
                shiftIfNecessary();
                return;
            }

            writeToTagInternal(str);
            shiftIfNecessary();
        }
    }

    protected int showNextSymbol() throws IOException {
        if (!isEnd()) {
            shiftIfNecessary();
            return buffer.getMainBuffer()[buffer.getCurrentBufferPosition()];
        } else {
            return -1;
        }
    }

    protected void shift() throws IOException {
        writeToTagInternal(buffer.getMainBuffer()[buffer.getCurrentBufferPosition()]);
    }

    protected void prepareBeforeNextToken() {
        clearTagInternalList();
    }

    public TOKEN_TYPE nextToken() throws IOException {
        prepareBeforeNextToken();

        if (isEmptyTag && type == START_TAG) {
            type = END_TAG;
            isEmptyTag = false;
            finishEmptyTag();
            return type;
        }

        type = peekType();

        if (type == null) {
            return null;
        }

        switch (type) {
            case UNICODEDECL:
                readUnicodeDecl();
                break;

            case XMLDECL:
                readXMLDecl();
                break;

            case PROCESSING_INSTRUCTION:
                readPI(false);
                break;

            case COMMENT:
                readComment();
                break;

            case START_TAG:
                readStartTag();
                break;

            case END_TAG:
                readEndTag();
                break;

            default:
                readText();
                break;
        }
        return type;
    }

    private TOKEN_TYPE peekType() throws IOException {

        if (buffer.getFilePosition() == 1) {
            int ch = showNextSymbol();
            if (ch == (char) 0xFEFF) {
                return UNICODEDECL;
            }
        }

        String token = getOneOfThem("</", "<?", "<!--", "<!", "<");

        if (isEnd()) {
            return END;
        }
        if (token != null) {
            return switch (token) {
                case "</" -> END_TAG;
                case "<?" -> XMLDECL;
                case "<!" -> PROCESSING_INSTRUCTION;
                case "<!--" -> COMMENT;
                case "<" -> START_TAG;
                default -> TEXT;
            };
        }
        return TEXT;
    }

    private boolean isEmptyTag = false;

    @Getter
    private String elementName;

    private final Map<String, String> elementAttrs = new HashMap<>();

    private TOKEN_TYPE type = null;

    protected Map<String, String> getElementAttrs() {
        return elementAttrs;
    }

    private static boolean isNameChar(int ch) {
        return Character.isLetterOrDigit(ch)
                || ((char) ch == ':')
                || ((char) ch == '-')
                || ((char) ch == '.' || ((char) ch == '_'));
    }

    protected void finishEmptyTag() throws IOException {
        // Default implementation. Method is overridden in child.
    }

    protected String readXMLDecl() throws IOException {
        int ch;
        boolean isEncodingFound = false;
        boolean stop = false;
        String encoding = UTF_8_ENCODING;

        while ((ch = showNextSymbol()) != -1 && ch != '?' && !stop) {
            StringBuilder sb = new StringBuilder();
            while ((ch = showNextSymbol()) != -1 && isNameChar(ch)) {
                sb.append((char) ch);
                shift();
            }

            if (isEncodingFound && !sb.toString().equalsIgnoreCase("")) {
                encoding = sb.toString().equals(encoding) ? UTF_8_ENCODING : sb.toString();
                stop = true;
            } else if (sb.toString().equalsIgnoreCase("encoding")) {
                isEncodingFound = true;
            }

            shift();
        }

        readUntilOccur(">");
        getOneOfThem(">");

        if (!encoding.equalsIgnoreCase(UTF_8_ENCODING)) {
            return encoding;
        } else {
            return UTF_8_ENCODING;
        }
    }

    protected void readText() throws IOException {
        readUntilOccur("<");
    }

    protected void readPI(boolean fromDoctype) throws IOException {
        throw new IOException("processing instruction is not supported");
    }

    protected void readComment() throws IOException {
        readUntilOccur("-->");
        getOneOfThem("-->");
    }

    private void readS() throws IOException {

        while (true) {
            if (getOneOfThem((char) 0x20, (char) 0x9, (char) 0xD, (char) 0xA) <= 0) {
                return;
            }
        }
    }

    private void readUnicodeDecl() throws IOException {
        shift();
    }

    protected void readStartTag() throws IOException {

        buffer.setPrevStartElementPosition(buffer.getCurrentBufferPosition() - 1);
        buffer.setPrevStartElementCounter(buffer.getRealBufferPositionCounter());

        isEmptyTag = false;
        StringBuilder name = new StringBuilder();
        int ch;
        while ((ch = showNextSymbol()) != -1 && isNameChar(ch)) {
            shift();
            name.append((char) ch);
        }
        elementName = name.toString();

        elementAttrs.clear();

        readS();

        while ((ch = showNextSymbol()) != -1 && ch != '>' && ch != '/') {
            shift();
            name.setLength(0);
            name.append((char) ch);
            while ((ch = showNextSymbol()) != -1 && isNameChar(ch)) {
                shift();
                name.append((char) ch);
            }

            if (name.toString().contains("!DOCTYPE")) {
                break;
            }

            readS();
            shift();
            readS();

            int exitChar = showNextSymbol();
            shift();
            StringBuilder attrValue = new StringBuilder();
            while ((ch = showNextSymbol()) != -1) {
                shift();
                if (ch == exitChar) break;
                attrValue.append((char) ch);
            }
            readS();
            addAttribute(name.toString(), attrValue.toString());
        }

        shift();
        if (ch == '/') {
            shift();
            isEmptyTag = true;
        }
    }

    protected void addAttribute(String attrKey, String attrValue) {
        elementAttrs.put(attrKey, attrValue);
    }

    protected void readEndTag() throws IOException {

        StringBuilder name = new StringBuilder();
        int ch;
        while ((ch = showNextSymbol()) != -1 && isNameChar(ch)) {
            shift();
            name.append((char) ch);
        }
        elementName = name.toString();
        if (ch != '>') {
            readS();
        }
        shift();
    }
}
