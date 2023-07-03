package server.commands;

public enum Command {

    WHISPER("/whisper", new WhisperCommand()),
    HELP("/help", new HelpCommand()),
    LIST("/list", new ListCommand()),
    QUIT("/quit", new QuitCommand());

    private final String description;
    private final CommandHandler commandHandler;

    Command(String description, CommandHandler commandHandler) {
        this.description = description;
        this.commandHandler = commandHandler;
    }

    public static Command getCommandFromDescription(String description) {
        for(Command cmd : values()){
            if(cmd.getDescription().equals(description)){
                return cmd;
            }
        }
        return null;
    }


    public String getDescription() {
        return description;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
