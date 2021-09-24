package web;

import com.google.gson.Gson;
import pojo.User;
import service.UserService;
import service.impl.UserServiceImpl;
import utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY;

/**
 * @author HALF
 * @create 2021-08-21 11:10
 */
public class UserServlet extends BaseServlet {

    private UserService userService = new UserServiceImpl();

    protected void ajaxExistsUsername(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取请求的参数username
        String username = req.getParameter("username");
        // 调用userService.existsUsername();
        boolean existsUsername = userService.existsUsername(username);
        // 把返回的结果封装成为map对象
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("existsUsername",existsUsername);

        Gson gson = new Gson();
        String json = gson.toJson(resultMap);

        resp.getWriter().write(json);
    }

    /**
     * 处理注销的功能
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    protected void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1、销毁Session中用户登录的信息（或者销毁Session）
        req.getSession().invalidate();
        //2、重定向到首页（或登录页面）。
        resp.sendRedirect(req.getContextPath());
    }

    /**
     * 处理登录的功能
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1、获取请求的参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        // 调用 userService.login()登录处理业务
        User loginUser = userService.login(new User(null, username, password, null));
        // 如果等于 null,说明登录 失败
        if(loginUser == null){
            //把错误信息，和回显的表单项信息，保存到Request域中
            req.setAttribute("msg","用户名或密码错误！");
            req.setAttribute("username",username);
            // 跳回登录页
            req.getRequestDispatcher("/pages/user/login.jsp").forward(req,resp);
        }else{
            // 登录 成功
            //保存用户登录的信息到Session域中
            req.getSession().setAttribute("user",loginUser);
            //跳到成功页面 login_success.jsp
            req.getRequestDispatcher("/pages/user/login_success.jsp").forward(req,resp);

        }
    }

    /**
     * 处理注册的功能
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    protected void regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 获取Session中的验证码
        String token = (String) req.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        // 删除 Session中的验证码
        req.getSession().removeAttribute(KAPTCHA_SESSION_KEY);

        //1、获取请求的参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String code = req.getParameter("code");

        User user = WebUtils.copyParamToBean(req.getParameterMap(),new User());

        // 2、检查 验证码是否正确 === 写死,要求验证码为:abcde
        if(token != null && token.equalsIgnoreCase(code)){
            // 3、检查 用户名是否可用
            if(userService.existsUsername(username)){
                //用户名已存在，跳回注册页面
                System.out.println("用户名[" + username + "]已存在!");

                //把回显信息保存到request域中
                req.setAttribute("msg","用户名已存在！");
                req.setAttribute("username",username);
                req.setAttribute("email",email);

                req.getRequestDispatcher("/pages/user/regist.jsp").forward(req,resp);
            }else{
                //用户名可用
                //调用Service保存到服务器
                userService.registUser(new User(null, username, password, email));
                //跳转到注册成功页面
                req.getRequestDispatcher("/pages/user/regist_success.jsp").forward(req,resp);
            }
        }else{
            //把回显信息保存到request域中
            req.setAttribute("msg","验证码错误！");
            req.setAttribute("username",username);
            req.setAttribute("email",email);
            //不正确，跳回注册页面
            System.out.println("验证码[" + code + "]错误");
            //请求转发的/指的是web目录
            req.getRequestDispatcher("/pages/user/regist.jsp").forward(req,resp);
        }
    }

}
