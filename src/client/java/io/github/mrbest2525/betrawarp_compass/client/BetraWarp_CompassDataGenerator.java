package io.github.mrbest2525.betrawarp_compass.client;

//import io.github.mrbest2525.betrawarp_compass.client.datagen.BetraWarp_CompassModelProvider;
import io.github.mrbest2525.betrawarp_compass.client.datagen.BetraWarp_CompassRecipeProvider;
import io.github.mrbest2525.betrawarp_compass.client.datagen.CompassModelGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BetraWarp_CompassDataGenerator implements DataGeneratorEntrypoint {
    
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BetraWarp_CompassRecipeProvider::new);
        pack.addProvider(CompassModelGenerator::new);
    }
}
