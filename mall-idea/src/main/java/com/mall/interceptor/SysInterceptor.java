package com.mall.interceptor;

import com.mall.exception.AuthenticationException;
import com.mall.util.JwtUtils;
import com.mall.util.StringUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 鉴权拦截器
 */
@Component
public class SysInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(SysInterceptor.class);
    // 从配置文件中读取不需要鉴权的路径
    @Value("${interceptor.ignore.paths:/categories,/goods/list}")
    private String[] ignorePaths;

    // 构造函数或setter方法注入ignorePaths
    public SysInterceptor(List<String> ignorePaths) {
        if (ignorePaths == null) {
            logger.error("ignorePaths is null");
        } else {
            this.ignorePaths = ignorePaths.toArray(new String[0]);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        logger.info("Intercepting request for path: {}", path);

        // 放行静态资源请求和配置的忽略路径
        if (isStaticResource(path) || isIgnoredPath(path)) {
            logger.info("Request for path {} is ignored.", path);
            return true;
        }

        String token = request.getHeader("token");
        logger.info("Token received: {}", token);

        if (StringUtil.isEmpty(token)) {
            logger.warn("Empty token for path: {}", path);
            throw new AuthenticationException("签名验证不存在");
        }

        Claims claims = JwtUtils.validateJWT(token).getClaims();
        logger.info("Claims validated: {}", claims);

        // 管理员路径检查
        if (path.startsWith("/admin")) {
            if (claims == null || !"admin".equals(claims.getSubject()) || !"-1".equals(claims.getId())) {
                logger.error("Admin authentication failed for path: {}", path);
                throw new AuthenticationException("鉴权失败！");
            }
            logger.info("Admin authentication successful");
            return true;
        }

        // 普通用户鉴权
        if (claims == null) {
            logger.error("User authentication failed for path: {}", path);
            throw new AuthenticationException("鉴权失败！");
        }

        logger.info("User authentication successful");
        return true;
    }

    /**
     * 判断是否是静态资源请求
     */
    private boolean isStaticResource(String path) {
        return path.startsWith("/image/") ||
                path.startsWith("/new-static/") ||
                path.endsWith(".js") ||
                path.endsWith(".css") ||
                path.endsWith(".jpg") ||
                path.endsWith(".jpeg") ||
                path.endsWith(".png") ||
                path.endsWith(".gif") ||
                path.endsWith(".ico");
    }

    /**
     * 判断是否是配置的忽略路径
     */
    private boolean isIgnoredPath(String path) {
        if (ignorePaths == null) {
            logger.error("ignorePaths is null");
            return false;
        }
        List<String> ignorePathList = Arrays.asList(ignorePaths);
        return ignorePathList.contains(path);
    }
}