package br.com.diefenthaeler.matheus.aws_project01.model;

import br.com.diefenthaeler.matheus.aws_project01.enums.EventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Envelope {
    private EventType eventType;
    private String data;
}
