package pers.tgl.mikufans.form;

import pers.tgl.mikufans.model.Option;

import java.util.Collections;
import java.util.List;

public interface OptionsProvider {
    default List<Option> getOptions() {
        return Collections.emptyList();
    }
}
