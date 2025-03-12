package pers.tgl.mikufans.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.interfaces.IMessageModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageModel implements IMessageModel {
    private BusinessType businessType;
    private String uri;
    private String message;

    public String getBusiness() {
        return businessType.getLabel();
    }
}