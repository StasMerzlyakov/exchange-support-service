package ru.otus.exchange.fxml;

import lombok.Getter;
import lombok.Setter;

/**
 * Примитивный буфер
 */
public class SimpleBuffer implements IBuffer {

    protected BufferPositions positions = new BufferPositions();

    @Getter
    @Setter
    char[] mainBuffer;

    public int getBufferPosition(char symbol) {
        int position = positions.currentBufferPosition - 1;
        int counter = positions.realBufferPositionCounter;

        while (position > -1) {
            if (mainBuffer[position] == symbol
                    &&
                    // проверка на <...></...>
                    !(position > 0 && mainBuffer[position - 1] == '>' && mainBuffer[position + 1] == '/')) {
                break;
            }
            --position;
        }

        if (position == -1) {
            position = positions.prevStartElementPosition;
            counter = positions.prevStartElementCounter;
        }

        if (symbol == '>') position++;

        return CHARACTER_ARRAY_BUFFER_SIZE / 2 * counter + position + positions.cyrillicSymbols;
    }

    public int getMainBufferRealLen() {
        return positions.mainBufferRealLen;
    }

    public void setMainBufferRealLen(int length) {
        this.positions.mainBufferRealLen = length;
    }

    public int getFilePosition() {
        return positions.filePosition;
    }

    public void setFilePosition(int filePosition) {
        this.positions.filePosition = filePosition;
    }

    public int getRealBufferPositionCounter() {
        return positions.realBufferPositionCounter;
    }

    public int getCurrentBufferPosition() {
        return positions.currentBufferPosition;
    }

    public void setCurrentBufferPosition(int position) {
        this.positions.currentBufferPosition = position;
    }

    public void incBufferPositionCounter() {
        ++this.positions.realBufferPositionCounter;
    }

    public void setPrevStartElementPosition(int position) {
        this.positions.prevStartElementPosition = position;
    }

    public void setPrevStartElementCounter(int counter) {
        this.positions.prevStartElementCounter = counter;
    }

    public void incCyrillicSymbols() {
        ++this.positions.cyrillicSymbols;
    }

    public void addToCurrentBufferPosition(int length) {
        this.positions.currentBufferPosition += length;
    }

    public void decCurrentBufferPosition(int length) {
        this.positions.currentBufferPosition -= length;
    }

    public void addToFilePosition(int length) {
        this.positions.filePosition += length;
    }

    public void incFilePosition() {
        ++this.positions.filePosition;
    }

    public boolean rollback() {
        return false;
    }
}
