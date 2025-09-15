package ru.otus.exchange.fxml;

public interface IBuffer {

    int CHARACTER_ARRAY_BUFFER_SIZE = 1024;

    int getBufferPosition(char symbol);

    void setMainBuffer(char[] array);

    char[] getMainBuffer();

    void setPrevStartElementPosition(int position);

    void setPrevStartElementCounter(int counter);

    int getCurrentBufferPosition();

    void setCurrentBufferPosition(int position);

    int getMainBufferRealLen();

    void setMainBufferRealLen(int length);

    int getFilePosition();

    void setFilePosition(int filePosition);

    void incCyrillicSymbols();

    int getRealBufferPositionCounter();

    void incBufferPositionCounter();

    void addToCurrentBufferPosition(int length);

    void decCurrentBufferPosition(int length);

    void addToFilePosition(int length);

    void incFilePosition();

    boolean rollback();
}
