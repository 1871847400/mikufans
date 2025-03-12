package pers.tgl.mikufans.model;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import pers.tgl.mikufans.domain.base.BaseEntity;
import pers.tgl.mikufans.es.ElasticAutoSync;
import pers.tgl.mikufans.es.FieldInfo;

import java.util.Collections;
import java.util.List;

@Data
public class ElasticSyncCache {
    private Class<? extends BaseDoc> documentClass;
    private ElasticAutoSync annotation;
    private Document document;
    private List<Table> tables;
    private List<Join> joins;

    public Table getMainTable() {
        return tables.get(0);
    }

    public List<Table> getSubTables() {
        return tables.size() <= 1 ? Collections.emptyList() : tables.subList(1, tables.size());
    }

    public Table getTable(Class<?> tableClass) {
        for (Table table : tables) {
            if (table.getTableClass().equals(tableClass)) {
                return table;
            }
        }
        return null;
    }

    @Data
    public static class Join {
        private ElasticAutoSync.Join join;
        //连接的目标表
        private Table joinTable;
        //发起连接的表和字段
        private FieldInfo thisField;
        //连接目标的目标字段
        private FieldInfo joinField;

        public Join(ElasticAutoSync.Join join) {
            this.join = join;
        }
    }

    @Data
    @AllArgsConstructor
    public static class Table {
        private TableInfo tableInfo;
        private String alias;
        public Class<?> getTableClass() {
            return tableInfo.getEntityType();
        }
        public String getTableName() {
            return tableInfo.getTableName();
        }
        public String getAliasName() {
            return alias + "." + getTableName();
        }
        public String getKeyColumn() {
            return tableInfo.getKeyColumn();
        }
        public String getDeleteColumn() {
            return tableInfo.getLogicDeleteFieldInfo().getColumn();
        }
        public String getUpdateColumn() {
            for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
                if (tableFieldInfo.getProperty().equals(BaseEntity.Fields.updateTime)) {
                    return tableFieldInfo.getColumn();
                }
            }
            return null;
        }
    }
}