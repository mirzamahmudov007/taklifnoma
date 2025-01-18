package com.taklifnoma.taklifnomalar.bot;

import com.taklifnoma.taklifnomalar.entity.Taklifnoma;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSession {
    private BotState state;
    private Taklifnoma taklifnoma;
    private String lastQuestion;

    public UserSession() {
        this.state = BotState.START;
        this.taklifnoma = new Taklifnoma();
    }

    public BotState getState() {
        return state;
    }

    public void setState(BotState state) {
        this.state = state;
    }

    public Taklifnoma getTaklifnoma() {
        return taklifnoma;
    }

    public void setTaklifnoma(Taklifnoma taklifnoma) {
        this.taklifnoma = taklifnoma;
    }

    public String getLastQuestion() {
        return lastQuestion;
    }

    public void setLastQuestion(String lastQuestion) {
        this.lastQuestion = lastQuestion;
    }
}

