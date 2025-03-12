package pers.tgl.mikufans.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.message.UserDialog;
import pers.tgl.mikufans.model.BaseSearch;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDialogSearch extends BaseSearch<UserDialog> {
}