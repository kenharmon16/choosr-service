package com.choosr.choosr_service.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class DecisionDTO implements Serializable{
    @Serial
    private static final long serialVersionUID = 2936764645138286983L;
    public String title;
    public String description;
}
