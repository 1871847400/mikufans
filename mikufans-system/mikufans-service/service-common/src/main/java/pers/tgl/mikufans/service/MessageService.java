package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.model.MessageModel;

public interface MessageService {
    MessageModel getModel(BusinessType type, Long id);
}
