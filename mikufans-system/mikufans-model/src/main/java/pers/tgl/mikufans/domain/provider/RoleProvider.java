package pers.tgl.mikufans.domain.provider;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import pers.tgl.mikufans.domain.system.SysRole;
import pers.tgl.mikufans.form.OptionsProvider;
import pers.tgl.mikufans.model.Option;

import java.util.List;
import java.util.stream.Collectors;

public class RoleProvider implements OptionsProvider {
    @Override
    public List<Option> getOptions() {
        return Db.list(SysRole.class)
                .stream()
                .map(a -> new Option(a.getRoleName(), a.getId()))
                .collect(Collectors.toList());
    }
}