package ru.otus.exchange.fxml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.xml.namespace.QName;
import lombok.Getter;
import lombok.Setter;

public class NamespaceResolverXMLParser extends CharArrXMLParser {

    @Getter
    @Setter
    private Map<String, LinkedList<String>> attributeMap;

    @Getter
    private final Map<String, String> freshAttrMap;

    private final Deque<String> prefixList; // Префиксы, которые нужно удалить после того, как вышли из element

    private final Deque<QName> elementPath;

    private static final String SEPARATOR = "!"; // '!' не может быть стартовым символом имени
    private static final String XMLNS = "xmlns";
    private static final String COLON = ":";

    @Getter
    @Setter
    private String elementLocalName;

    @Getter
    @Setter
    private String elementNamespace;

    @Getter
    @Setter
    private String elementPrefix;

    @Getter
    @Setter
    private Boolean attributeFormDefault = false; // false = unqualifier; true = qualifier

    // Флаг, указывающий, что ранее был считан END_TAG и нужно выкинуть узел из elementPath.
    private boolean doPopPath = false;

    public NamespaceResolverXMLParser(InputStream inputStream) throws IOException {
        this(inputStream, new SimpleBuffer());
    }

    public NamespaceResolverXMLParser(InputStream inputStream, IBuffer buffer) throws IOException {
        super(inputStream, buffer);
        attributeMap = new LinkedHashMap<>();
        freshAttrMap = new LinkedHashMap<>();
        prefixList = new LinkedList<>();
        elementPath = new LinkedList<>();
    }

    private void push(String prefix, String value) {
        // Запоминаем атрибуты и пространства имен
        LinkedList<String> list = new LinkedList<>();
        // Сюда попадают пространства имен у текущего элемента, которые нужно будет убрать по выходу из него
        prefixList.add(prefix);
        list.push(value);
        // Тут все пространства имен со значениями, запоминаем
        if (attributeMap.get(prefix) == null) {
            attributeMap.put(prefix, list);
        } else {
            attributeMap.get(prefix).push(value);
        }
        // Тут дополнительные атрибуты
        freshAttrMap.put(prefix, value);
    }

    public String getCurrentPath() {
        StringBuilder fullName = new StringBuilder();
        for (Iterator<QName> iterator = elementPath.descendingIterator(); iterator.hasNext(); ) {
            fullName.append("/");
            fullName.append(iterator.next().toString());
        }
        return fullName.toString();
    }

    private void pop(String prefix) {
        if (prefix.startsWith(XMLNS)) {
            LinkedList<String> list = attributeMap.get(prefix);
            list.pop();
        }
    }

    protected String getCurrentNamespaceByPrefix(String prefix) throws IOException {
        String pref = prefix.isEmpty() ? XMLNS : String.join(":", XMLNS, prefix);
        LinkedList<String> list = attributeMap.get(pref);
        if (list == null) {
            list = new LinkedList<>();
            attributeMap.put(prefix, list);
        }

        if (!list.isEmpty()) {
            return list.getFirst();
        }

        // simple xml
        if (prefix.isEmpty()) {
            return "";
        }
        throw new IOException(String.format("can't find namespace by prefix '%s'", prefix));
    }

    // Будем считать что в имени только один ':'
    private String[] getNameWithNS(String name) {
        String[] result;
        if (XMLNS.equals(name)) {
            // Это всегда префикс
            result = new String[] {XMLNS, ""};
            return result;
        }
        if (!name.contains(COLON)) {
            result = new String[] {"", name};
            return result;
        }
        result = name.split(COLON);
        return result;
    }

    @Override
    protected void readStartTag() throws IOException {
        super.readStartTag(); // Это важно оставить

        // Если это пустой элемент, то нам он особо не нужен

        prefixList.add(SEPARATOR);

        freshAttrMap.clear();

        // 1. Определяемся с атрибутами xmlns
        for (Map.Entry<String, String> entry : getElementAttrs().entrySet()) {
            String attrName = entry.getKey();
            push(attrName, entry.getValue());
        }

        // 2. Определяем в каком пространстве имен находится узел
        // В исходном коде в name записывается префикс:имя
        String[] nameWithNS = getNameWithNS(getElementName());
        elementLocalName = nameWithNS[1];
        elementNamespace = getCurrentNamespaceByPrefix(nameWithNS[0]);
        elementPath.push(new QName(elementNamespace, elementLocalName));
        elementPrefix = elementNamespace != null ? nameWithNS[0] : null;
    }

    @Override
    protected void finishEmptyTag() throws IOException {
        super.finishEmptyTag();

        String[] nameWithNS = getNameWithNS(getElementName());
        elementLocalName = nameWithNS[1];
        elementNamespace = getCurrentNamespaceByPrefix(nameWithNS[0]);
        elementPrefix = elementNamespace != null ? nameWithNS[0] : null;
        doPopPath = true;

        // Выбрасываем из attrMap все что было определено в этом элементе
        // Пока не дойдем до "!", выбрасываем пр-ва имен, чтобы не было путаницы
        String prefix;
        while (!SEPARATOR.equals((prefix = prefixList.pollLast()))) {
            pop(prefix);
        }
    }

    @Override
    protected void prepareBeforeNextToken() {
        super.prepareBeforeNextToken();
        if (doPopPath) {
            elementPath.pop();
            doPopPath = false;
        }
    }

    @Override
    protected void readEndTag() throws IOException {
        super.readEndTag(); // Это важно оставить

        String[] nameWithNS = getNameWithNS(getElementName());
        elementLocalName = nameWithNS[1];
        elementNamespace = getCurrentNamespaceByPrefix(nameWithNS[0]);
        elementPrefix = elementNamespace != null ? nameWithNS[0] : null;
        doPopPath = true;

        // Выбрасываем из attrMap все что было определено в этом элементе
        // Пока не дойдем до "!", выбрасываем пр-ва имен, чтобы не было путаницы
        String prefix;
        while (!SEPARATOR.equals((prefix = prefixList.pollLast()))) {
            pop(prefix);
        }
    }
}
