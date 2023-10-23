package invaders.singleton;

import invaders.ConfigReader;

public class DifficultyManager {

    private ConfigReader configReader;

    String config = "";

    public DifficultyManager(){
        this.configReader = new ConfigReader();
    }

    public ConfigReader getInstance(){
        return this.configReader;
    }

    public void changeInstance(String config){
        this.config = config;
        this.configReader.parse(config);
    }

}