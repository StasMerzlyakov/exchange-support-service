package ru.otus.exchange.fxml;

import static ru.otus.exchange.fxml.IXmlParser.TOKEN_TYPE.END;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.xml.namespace.QName;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("java:S1075")
@Slf4j
public class XPathSearcher extends NamespaceResolverXMLParser {

    public XPathSearcher(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    public XPathSearcher(InputStream inputStream, IBuffer buffer) throws IOException {
        super(inputStream, buffer);
    }

    public static String joinStringStack(List<String> path) {
        return String.join("/", path);
    }

    public static String joinStackToString(List<String> path) {
        return "/" + joinStringStack(path);
    }

    @Getter
    public static class Pair<T, K> {
        private T first;

        private K second;

        public Pair() {}

        public Pair(T first, K second) {
            this.first = first;
            this.second = second;
        }
    }

    public static Pair<String, String> splitPathIntoParentAndChild(String currentPath) {
        if (!currentPath.startsWith("/")) {
            currentPath = "/" + currentPath;
        }
        String[] paths = currentPath.split("/\\{");

        int len = paths.length;
        if (len == 0) {
            return new Pair<>(null, null);
        }
        if (len == 1) {
            return new Pair<>(null, null);
        }

        String lastElement = "{" + paths[len - 1];
        String parent;
        if (len == 2) {
            parent = "/";
        } else {
            String[] parentPaths = Arrays.copyOfRange(paths, 0, len - 1);
            parent = String.join("/{", parentPaths);
        }
        return new Pair<>(parent, lastElement);
    }

    public List<String> getChildElementsQNameByPath(String parentQNamePath) throws NotXmlException {
        List<String> resultList = new LinkedList<>();
        IXmlParser.TOKEN_TYPE tokenType;
        try {
            while ((tokenType = nextToken()) != END) {
                if (Objects.requireNonNull(tokenType) == TOKEN_TYPE.START_TAG) {
                    String path = getCurrentPath();
                    Pair<String, String> parentAndChild = splitPathIntoParentAndChild(path);
                    if (parentQNamePath.equals(parentAndChild.first)) {
                        resultList.add(parentAndChild.second);
                    }
                }
            }
        } catch (Exception exception) {
            throw new NotXmlException(exception);
        }
        return resultList;
    }

    public List<String> getValuesByPath(String qNamePath) throws NotXmlException {

        if (!qNamePath.startsWith("/")) qNamePath = "/" + qNamePath;

        List<String> resultList = new LinkedList<>();

        IXmlParser.TOKEN_TYPE tokenType;

        try {
            while ((tokenType = nextToken()) != END) {
                if (Objects.requireNonNull(tokenType) == TOKEN_TYPE.TEXT) {
                    String currentPath = getCurrentPath();
                    if (qNamePath.equals(currentPath)) resultList.add(new String(getTagInternal()));
                }
            }
        } catch (Exception exception) {
            throw new NotXmlException(exception);
        }
        return resultList;
    }

    @SuppressWarnings("java:S3776")
    public List<QName> getQNameByPath(String qNamePath) throws NotXmlException {

        if (!qNamePath.startsWith("/")) qNamePath = "/" + qNamePath;

        List<QName> resultList = new LinkedList<>();

        IXmlParser.TOKEN_TYPE tokenType;
        try {
            while ((tokenType = nextToken()) != END) {
                if (Objects.requireNonNull(tokenType) == TOKEN_TYPE.TEXT) {
                    String currentPath = getCurrentPath();
                    if (qNamePath.equals(currentPath)) {

                        String value = new String(getTagInternal()).trim();
                        if (!value.isEmpty()) {
                            String[] valueArr = value.split(":");
                            String namespaceURI;
                            String localPart;
                            String prefix;

                            if (valueArr.length != 1) {
                                prefix = valueArr[0];
                                localPart = valueArr[1];
                            } else {
                                prefix = "";
                                localPart = value;
                            }

                            namespaceURI = getCurrentNamespaceByPrefix(prefix);
                            if (namespaceURI == null) {
                                throw new NotXmlException("can't evaluate namespace by prefix " + prefix);
                            }

                            resultList.add(new QName(namespaceURI, localPart));
                        }
                    }
                }
            }
        } catch (Exception exception) {
            throw new NotXmlException(exception);
        }
        return resultList;
    }

    public Map<String, List<String>> getValuesByQNamePathSet(Set<String> qNamePathSet) throws NotXmlException {
        Map<String, List<String>> resultMap = new HashMap<>();
        Set<String> searchQNamePathSet = new HashSet<>();
        qNamePathSet.forEach(qNamePath -> {
            if (!qNamePath.startsWith("/")) searchQNamePathSet.add("/" + qNamePath);
            else searchQNamePathSet.add(qNamePath);
        });
        IXmlParser.TOKEN_TYPE tokenType;

        try {
            while ((tokenType = nextToken()) != END) {
                if (Objects.requireNonNull(tokenType) == TOKEN_TYPE.TEXT) {
                    String currentPath = getCurrentPath();
                    if (searchQNamePathSet.contains(currentPath)) {
                        resultMap
                                .computeIfAbsent(currentPath, s -> new LinkedList<>())
                                .add(new String(getTagInternal()));
                    }
                }
            }
        } catch (Exception exception) {
            throw new NotXmlException(exception);
        }
        return resultMap;
    }

    /**
     * Обработка xml и извлечение указанных в xPath значений полей.
     *
     * @param qnamePathList   - список elementPath( {qname1}name1/{qname2}name2/...).
     *                        При этом, если элемент, на который указывает elementPathList, содержит значение,
     *                        возвращается значение; если вложенный элемент - возвращается qname элемента. В случае
     *                        нескольких элементов по данному пути или смешения значений и элементов реализация
     *                        вернет один из узлов. Если нужный путь не найден - выходная структура будет
     *                        содержать null
     * @param resolveQNameSet - Список узлов, текстовые значения которых нужно интерпретировать как значения QName
     *                        попытка разрешить значение (используется при извлечении qname из ReferenceQName)
     * @return карта - ключ-значение, где ключ - получается из List<QName> применением функции qNameListToString
     * @throws IOException
     */
    public Map<String, String> getValuesByPath(List<String> qnamePathList, Set<String> resolveQNameSet)
            throws NotXmlException {
        log.debug("getValuesByPath");

        // Инициализируем, если передан null
        if (resolveQNameSet == null) {
            resolveQNameSet = new HashSet<>();
        }

        // Карта поиска
        Map<String, String> resultMap = new HashMap<>();

        for (String qNamePath : qnamePathList) {
            resultMap.put(qNamePath, null);
        }

        // Текущий путь
        List<String> stackPath = new LinkedList<>();

        IXmlParser.TOKEN_TYPE tokenType;

        boolean tagExists = false;
        String currentQName = null;

        try {
            while ((tokenType = nextToken()) != END) {
                switch (tokenType) {
                    case START_TAG:
                        // Проверяем родительский тег на принадлежность resultMap
                        currentQName = new QName(getElementNamespace(), getElementLocalName()).toString();
                        tagExists = true;
                        String parentPath = joinStringStack(stackPath);
                        if (resultMap.containsKey(parentPath)) {
                            resultMap.put(parentPath, currentQName);
                        }
                        stackPath.add(currentQName);
                        break;
                    case TEXT:
                        processText(resultMap, stackPath, resolveQNameSet, currentQName);
                        break;
                    case END_TAG:
                        stackPath.removeLast();
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception exception) {
            throw new NotXmlException(exception);
        }
        if (!tagExists) throw new NotXmlException("xml-тегов в переданном массиве не найдено");

        if (!stackPath.isEmpty()) throw new NotXmlException("имеются не закрытые теги " + joinStringStack(stackPath));

        return resultMap;
    }

    @SuppressWarnings("java:S3776")
    private void processText(
            Map<String, String> resultMap, List<String> stackPath, Set<String> resolveQNameSet, String currentQName)
            throws IOException {
        String parentPath = joinStringStack(stackPath);
        if (resultMap.containsKey(parentPath)) {
            // Проверим текущее значение. Если это не null и не пустая строка, то добавим значение
            String currentValue = resultMap.get(parentPath);
            if (currentValue == null || currentValue.trim().isEmpty()) {
                if (resolveQNameSet.contains(currentQName)) {
                    // Текущий узел находится в списке узлов
                    String textWithQName = new String(getTagInternal());
                    String[] split = textWithQName.split(":");
                    String prefix;
                    String value;
                    if (split.length == 1) {
                        prefix = "";
                        value = textWithQName;
                    } else {
                        prefix = split[0];
                        // все что после prefix и ':'
                        value = textWithQName.substring(prefix.length() + 1);
                    }

                    String namespace = getCurrentNamespaceByPrefix(prefix);
                    if (namespace != null) {
                        // Если нашли что-то ненулевое  - значит нашли то что нужно
                        QName qname = new QName(namespace, value);
                        resultMap.put(parentPath, qname.toString());
                    } else {
                        // Ничего не нашил - пусть пользователи разбираются
                        resultMap.put(parentPath, currentValue);
                    }
                } else {
                    // Просто добавляем к результату текущее значение
                    resultMap.put(parentPath, new String(getTagInternal()));
                }
            }
        }
    }

    public Map<String, String> getValuesByPath(List<String> qNamePathList) throws NotXmlException {
        return getValuesByPath(qNamePathList, null);
    }
}
