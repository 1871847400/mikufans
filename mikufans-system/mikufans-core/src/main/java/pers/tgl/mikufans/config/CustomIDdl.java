package pers.tgl.mikufans.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import com.baomidou.mybatisplus.extension.ddl.DdlHelper;
import com.baomidou.mybatisplus.extension.ddl.DdlScript;
import com.baomidou.mybatisplus.extension.ddl.IDdl;
import com.baomidou.mybatisplus.extension.ddl.history.IDdlGenerator;
import com.baomidou.mybatisplus.extension.ddl.history.MysqlDdlGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.apache.commons.io.IOUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j(topic = "自动创建表")
public class CustomIDdl implements IDdl {
    private final DataSource dataSource;
    private final IDdlGenerator iddlGenerator = MysqlDdlGenerator.newInstance();
    private final CCJSqlParserManager parserManager = new CCJSqlParserManager();
    //匹配创建表语句的表名
    private static final Pattern pattern = Pattern.compile("(?<!\\s*#\\s*)create\\s+table\\s+(.+)", Pattern.CASE_INSENSITIVE);
    //加载sql的文件夹
    private static final String[] folders = new String[] {
            "sql/common",
            "sql/system",
            "sql/user"
    };

    @PostConstruct
    public void init() {
        if (isInitialized()) {
            return;
        }
        List<String> sqlList = new ArrayList<>();
        List<File> folderFiles = Arrays
                .stream(folders)
                .map(File::new)
                .collect(Collectors.toList());
        for (File folder : folderFiles) {
            if (!FileUtil.exist(folder)) {
                log.error("SQL文件夹 {} 不存在!", folder.getAbsolutePath());
                continue;
            }
            log.info("正在加载SQL文件夹: {}", folder.getAbsolutePath());
            String columnSql = getContent(folder, "column.sql");
            String defaultSql = getContent(folder, "default.sql");
            List<File> files = FileUtil.loopFiles(folder, 1, f -> f.getName().endsWith(".sql"));
            for (File file : files) {
                if (file.getName().equals("column.sql") || file.getName().equals("default.sql")) {
                    continue;
                }
                sqlList.addAll(loadSqlFile(file, columnSql));
            }
            if (defaultSql != null) {
                sqlList.add(defaultSql);
            }
        }
        log.info(StrUtil.join("\n\n", sqlList));
        try {
            new DdlScript(dataSource).run(StrUtil.join("\n", sqlList));
        } catch (Exception e) {
            log.error("执行SQL失败", e);
        }
    }

    private boolean isCreateTable(Statement statement, String sql) {
        return statement instanceof CreateTable || statement == null && sql.trim().toLowerCase().startsWith("create");
    }

    private String getContent(File folder, String filename) {
        File file = new File(folder, filename);
        return file.exists() ? FileUtil.readUtf8String(file) : null;
    }

    private List<String> loadSqlFile(File file, String columnSql) {
        log.info("加载sql文件: {}", file.getAbsolutePath());
        List<String> sqlList = new ArrayList<>();
        String tableSql = FileUtil.readUtf8String(file);
        for (String sql : tableSql.split(";")) {
            if (StrUtil.isBlank(sql)) {
                continue;
            }
            Statement statement = null;
            try {
                statement = parserManager.parse(new StringReader(sql));
            } catch (JSQLParserException e) {
                //创建表语句内有索引,Jsql会报错
            }
//            if (statement instanceof Drop) {
//                continue;
//            }
            sqlList.add(sql.trim() + ";");
            if (isCreateTable(statement, sql)) {
                Matcher matcher = pattern.matcher(sql);
                if (matcher.find()) {
                    String tableName = matcher.group(1);
                    log.info("检测到创建表: {}", tableName);
                    if (columnSql != null) {
                        sqlList.add(columnSql.replace("${}", tableName));
                    }
                }
            }
//            if (statement instanceof CreateIndex) {
//            }
        }
        return sqlList;
    }

    /**
     * 当前数据库表数量 > 0 表示已经初始化了
     */
    private boolean isInitialized() {
        try {
            return !Db.use(dataSource).query("show tables").isEmpty();
        } catch (SQLException e) {
            log.error("检查初始化失败", e);
            return true;
        }
    }

    @Override
    public void runScript(Consumer<DataSource> consumer) {
        //该方法执行优先级太低,执行之前别的业务调用了SQL
    }

    @Override
    public List<String> getSqlFiles() {
        return Collections.emptyList();
//        return Arrays.asList(
//                "sql/test2.sql"
//        );
    }
}