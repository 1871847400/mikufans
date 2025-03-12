package pers.tgl.mikufans.domain.provider;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import pers.tgl.mikufans.domain.system.ReportBehavior;
import pers.tgl.mikufans.form.OptionsProvider;
import pers.tgl.mikufans.model.Option;

import java.util.List;
import java.util.stream.Collectors;

public class ReportBehaviorProvider implements OptionsProvider {
    @Override
    public List<Option> getOptions() {
        return Db.list(ReportBehavior.class)
                .stream()
                .map(item -> new Option(item.getCategory(), item.getCategory()))
                .distinct()
                .collect(Collectors.toList());
    }
}