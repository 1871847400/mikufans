package pers.tgl.mikufans.controller.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.form.AppForm;

@RestController
@RequestMapping("/admin/form")
public class FormController extends BaseController {
    @GetMapping("/{name}")
    public AppForm getForm(@PathVariable String name) {
        return AppForm.getAppForm(name);
    }
}