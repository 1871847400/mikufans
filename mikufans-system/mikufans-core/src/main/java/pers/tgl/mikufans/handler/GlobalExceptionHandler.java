package pers.tgl.mikufans.handler;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import io.jsonwebtoken.JwtException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.util.R;

import javax.validation.ValidationException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，只会处理到controller抛出的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 单文件最大限制
     */
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;
    /**
     * 异常兜底
     */
    @ExceptionHandler(Exception.class)
    public R<?> handle(Exception e) {
        e.printStackTrace();
        return R.error("未知错误！");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handle(HttpRequestMethodNotSupportedException e) {
        return R.error("请求方式不支持");
    }
    /**
     *
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<?> handle(MethodArgumentTypeMismatchException e) {
        return R.error("请求参数类型不匹配！" + e.getPropertyName());
    }
    /**
     * controller
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public R<?> handle(HttpMessageConversionException e) {
        return R.error("请求参数类型错误！");
    }
    /**
     * 调用controller缺少参数
     */
    @ExceptionHandler(MissingRequestValueException.class)
    public R<?> handle(MissingRequestValueException e) {
        return R.error("缺少请求参数！");
    }
    /**
     * 通过springJPA操作数据库unique key导致的异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public R<?> handle(DuplicateKeyException e) {
        return R.error("请勿提交已存在的数据");
    }
    /**
     * 自定义异常
     */
    @ExceptionHandler(CustomException.class)
    public R<?> handle(CustomException e) {
        return e.getResult();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<?> handle(MaxUploadSizeExceededException e) {
        return R.error("上传文件最大不超过" + maxFileSize);
    }

    @ExceptionHandler(ValidationException.class)
    public R<?> handle(ValidationException e) {
        return R.error(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public R<?> handle(AuthenticationException e) {
        return R.error("账号或密码错误！");
    }

    /**
     * token解析失败
     */
    @ExceptionHandler(JwtException.class)
    public R<?> handle(JwtException e) {
        return R.error(Code.NOT_LOGIN);
    }

    /**
     * 未找到登录信息
     */
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public R<?> handle(AuthenticationCredentialsNotFoundException e) {
        return R.error(Code.NOT_LOGIN);
    }

    /**
     * springSecurity
     */
    @ExceptionHandler(AccessDeniedException.class)
    public R<?> handle(AccessDeniedException e) {
        return R.error("您没有权限访问");
    }

    /**
     * 超出数据库的字段大小限制
     */
    @ExceptionHandler(MysqlDataTruncation.class)
    public R<?> handle(MysqlDataTruncation e) {
        return R.error("超出允许的数据范围");
    }

    /**
     * 数据校验异常
     */
    @ExceptionHandler(BindException.class)
    public R<?> handle(BindException e) {
        return R.error(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
    /**
     * 通过Mybatis操作数据库内的异常
     */
    @ExceptionHandler(PersistenceException.class)
    public R<?> handle(PersistenceException e) {
        if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
            String message = e.getCause().getMessage();
            if (message.contains("Duplicate entry")) {
                String content = message.substring(message.indexOf("'")+1, message.indexOf("-\\x00"));
                return R.error( content+"已经存在了");
            }
        }
        return R.error("参数不合法");
    }

    @ExceptionHandler(DisabledException.class)
    public R<?> handle(DisabledException e) {
        return R.error(Code.AUTH_DISABLED);
    }

    /**
     * 前端发送的数据转换为后端的数据格式时发生的异常
     * 例如字符串转为枚举时
     */
    @ExceptionHandler(ConversionException.class)
    public R<?> handle(ConversionException e) {
        return R.error("参数格式错误");
    }
}