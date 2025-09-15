package ru.otus.exchange.fxml;

public class BufferPositions {
    int mainBufferRealLen;
    int currentBufferPosition;
    int filePosition;
    int realBufferPositionCounter;
    int prevStartElementPosition;
    int prevStartElementCounter;
    int cyrillicSymbols;

    public BufferPositions() {}

    public BufferPositions(BufferPositions template) {
        mainBufferRealLen = template.mainBufferRealLen;
        currentBufferPosition = template.currentBufferPosition;
        filePosition = template.filePosition;
        realBufferPositionCounter = template.realBufferPositionCounter;
        prevStartElementPosition = template.prevStartElementPosition;
        prevStartElementCounter = template.prevStartElementCounter;
        cyrillicSymbols = template.cyrillicSymbols;
    }
}
