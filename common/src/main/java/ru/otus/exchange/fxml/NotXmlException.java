package ru.otus.exchange.fxml;

/**
 * Ошибка, указывающая, что передан не xml.
 * @see XPathSearcher#getValuesByQNamePathSet
 */
public class NotXmlException extends Exception {
    public NotXmlException(String s) {
        super(s);
    }

    public NotXmlException(Exception e) {
        super(e);
    }
}
