package pers.tgl.mikufans.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Option {
    private String label;
    /**
     * string / number/ boolean
     */
    private Object value;
}