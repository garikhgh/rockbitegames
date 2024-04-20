package com.rockbitegames.validator;


import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.dto.PlayerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PayerValidate {
    private static final Logger logger = LoggerFactory.getLogger(PayerValidate.class);
    private PayerValidate() {throw new ExceptionInInitializerError("Not allowed to create object.");}

    public static PlayerValidator validate(PlayerDto player) {
        PlayerValidator playerValidator = new PlayerValidator();

        if (player.getNumberOfWarehouses()<= 0) {
            playerValidator.setError("Warehouse Number Could be Zero or Negative");
        }
        if (player.getPlayerUuid().isBlank() || player.getPlayerUuid().isEmpty()) {
            playerValidator.setError("Player Uuid Could not blank or empty.");
        }
        if (player.getPlayerEmail().isBlank() || player.getPlayerEmail().isEmpty()) {
            playerValidator.setError("Player Email Could not blank or empty.");
        }

        if (player.getPlayerLevel().isBlank() || player.getPlayerLevel().isEmpty()) {
            playerValidator.setError("Player Level Could not blank or empty.");
        }

        if (player.getPlayerName().isBlank() || player.getPlayerName().isEmpty()) {
            playerValidator.setError("Player Name Could not blank or empty.");
        }

        return playerValidator;
    }


    @Getter
    @NoArgsConstructor
    public static class PlayerValidator {
        private int errorCounter;
        private final List<String> error = new ArrayList<>();

        public void setError(String error) {
            this.error.add(error);
            this.errorCounter ++;
        }
    }
}
