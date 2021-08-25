package bot.assembly.memory;

import bot.assembly.task.Task;
import bot.assembly.task.Deadline;
import bot.assembly.task.Event;
import bot.assembly.task.ToDo;
import bot.assembly.function.BotTemporalUnit;
import bot.assembly.error.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class BotDynamicMemoryUnit {

    BotStaticMemoryUnit botStaticMemoryUnit = new BotStaticMemoryUnit();
    BotTemporalUnit botTemporalUnit = new BotTemporalUnit();

    public final String HARD_DISK_DATA_NAME = "data.txt";
    public final String HARD_DISK_DATA_STORAGE_DIRECTORY = System.getProperty("user.home");

    public List<Task> taskTracker = new ArrayList<Task>();
    private static BotDynamicMemoryUnit dynamicMemoryUnit = null;

    public BotDynamicMemoryUnit() {}

    public String produceStringData(){
        StringBuilder outputData = new StringBuilder();
        taskTracker.stream().forEach(x -> outputData.append(generateEasyDataTaskFormat(x)));
        return outputData.toString();
    }

    public String generateEasyDataTaskFormat(Task task){

        String taskType = task.getTaskType();
        switch (taskType){
            case "T": {
                return String.format(
                        "%s | %s | %s\n",
                        task.getTaskType(),
                        task.getIsDone(),
                        task.getTaskTitle()
                );
            }

            default: {
                return String.format(
                        "%s | %s | %s | %s\n",
                        task.getTaskType(),
                        task.getIsDone(),
                        task.getTaskTitle(),
                        task.getDateTime()
                );
            }
        }
    }

    public void saveToHardDisk() throws IOException {
        FileWriter fw = new FileWriter(
                String.format(
                        "%s/%s",
                        HARD_DISK_DATA_STORAGE_DIRECTORY,
                        HARD_DISK_DATA_NAME
                ),
                false
        );
        fw.write(produceStringData());
        fw.close();
    }

    public String[] tokenizeData(String input) {
        return input.split(" \\| ");
    }

    public Task decipherStringData(String stringData) throws InvalidDataFormatException {
        String[] stringDataToken = tokenizeData(stringData);
        String stringDataType = stringDataToken[0];
        try {
            switch (stringDataType){
                case "T":
                    return new ToDo(
                            stringDataToken[1].equals("true") ? true : false,
                            stringDataToken[2]);
                case "D":
                    return new Deadline(
                            stringDataToken[1].equals("true") ? true : false,
                            stringDataToken[2],
                            botTemporalUnit.convertStringToTemporalData(stringDataToken[3]));
                case "E":
                    return new Event(
                            stringDataToken[1].equals("true") ? true : false,
                            stringDataToken[2],
                            botTemporalUnit.convertStringToTemporalData(stringDataToken[3]));
                default:
                    throw new InvalidDataFormatException(
                            String.format(botStaticMemoryUnit.ERROR_MESSAGE_INVALID_DATA_FORMAT, stringData));
            }
        } catch (InvalidDataFormatException e) {
            throw new InvalidDataFormatException(
                    String.format(botStaticMemoryUnit.ERROR_MESSAGE_INVALID_DATA_FORMAT, stringData));
        }
    }

    public void loadFromHardDisk() throws IOException {
        try {
            File dataFile = new File(
                    String.format(
                            "%s/%s",
                            HARD_DISK_DATA_STORAGE_DIRECTORY,
                            HARD_DISK_DATA_NAME
                    )
            );

            if (dataFile.createNewFile()) {
                return;
            }

            Scanner myReader = new Scanner(dataFile);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                taskTracker.add(decipherStringData(data));
            }
            myReader.close();
        } catch (IOException e) {
            throw new InvalidFileException(botStaticMemoryUnit.ERROR_MESSAGE_INVALID_FILE);
        }
    }

    public static BotDynamicMemoryUnit getInstance() {
        if (dynamicMemoryUnit == null) {
            dynamicMemoryUnit = new BotDynamicMemoryUnit();
        }
        return dynamicMemoryUnit;
    }
}