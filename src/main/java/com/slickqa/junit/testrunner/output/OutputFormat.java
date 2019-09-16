package com.slickqa.junit.testrunner.output;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.slickqa.junit.testrunner.Configuration;
import com.slickqa.junit.testrunner.TerminalWidthProvider;
import de.vandermeer.asciitable.AsciiTable;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.List;

public enum OutputFormat {
    table(null),
    json(new JsonFactory()),
    yaml(new YAMLFactory());

    public static String COLUMN_WIDTH_OPTION = "COLUMN_WIDTH_OPTION";
    private JsonFactory factory;

    OutputFormat(JsonFactory factory) {
        this.factory = factory;
    }

    public String generateOutput(List<? extends EndUserData> data, Configuration... options) {
        if(data == null || data.size() == 0) {
            return "";
        }
        if(this == table) {
            AsciiTable table = new AsciiTable();
            table.addRule();
            data.get(0).addColumnHeadersToTable(table, options);
            table.addRule();
            for(EndUserData item : data) {
                item.addToTable(table, options);
                table.addRule();
            }
            int width = TerminalWidthProvider.width();
            String widthOption = Configuration.GetOptionIfSet(options, COLUMN_WIDTH_OPTION);
            if(widthOption != null) {
                width = Integer.parseInt(widthOption);
            }
            table.getContext().setWidth(width);
            table.getRenderer().setCWC(new SmartColumnWidthCalculator());
            return table.render();
        }
        ObjectMapper mapper = new ObjectMapper(factory);
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
