package io.jenkins.plugins.icqnotifications.utils;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IcqKeyBoard {

    private final ArrayList<ArrayList<IcqBaseButton>> rows = new ArrayList<>();

    public IcqKeyBoard() {

    }

    public void addButton(IcqBaseButton button){

        ArrayList<IcqBaseButton> newRow = new ArrayList<IcqBaseButton>();

        newRow.add(button);

        rows.add(
                newRow
        );
    }

    public void addButtonsRow(ArrayList<IcqBaseButton> buttonsRow){
        rows.add(buttonsRow);
    }

    @Override
    public String toString(){

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(rows);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
