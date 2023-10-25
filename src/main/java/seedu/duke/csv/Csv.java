package seedu.duke.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.opencsv.CSVWriter;
import seedu.duke.exception.DukeException;

public class Csv {
    private static final String FILE_PATH = "./Exports/";
    private static final String FILE_NAME = "transactions.csv" ;
    private static Csv csvFile = null;
    private CSVWriter writer;

    public Csv() throws DukeException {
        try {
            File directory = new File(FILE_PATH);
            directory.mkdir();
            Writer fileWriter = new FileWriter(FILE_PATH + FILE_NAME, false);
            this.writer = new CSVWriter(fileWriter);
        } catch (IOException e) {
            throw new DukeException("Cannot create file");
        }
    }

    public void write(String[] data) {
        assert writer != null;
        writer.writeNext(data);
    }

    public void close() throws DukeException {
        try {
            writer.close();
        } catch (IOException e) {
            throw new DukeException("Error Closing File");
        }
    }
}
