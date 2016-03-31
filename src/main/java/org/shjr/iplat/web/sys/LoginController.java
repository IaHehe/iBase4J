package org.shjr.iplat.web.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shjr.iplat.core.Constants;
import org.shjr.iplat.core.exception.ParameterException;
import org.shjr.iplat.core.util.RedisUtil;
import org.shjr.iplat.core.util.SecurityUtil;
import org.shjr.iplat.core.util.WebUtil;
import org.shjr.iplat.service.sys.SysUserService;
import org.shjr.iplat.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController extends BaseController {
	@Autowired
	private SysUserService sysUserService;

	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelMap login(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
			@RequestParam(value = "account", required = true) String account,
			@RequestParam(value = "password", required = true) String password) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("usable", 1);
		params.put("account", account);
		params.put("password", SecurityUtil.encryptSHA(password));
		List<Map<String, Object>> list = sysUserService.query(params);
		if (list.size() == 1) {
			Map<String, Object> user = list.get(0);
			WebUtil.saveCurrentUser(request, response, user.get("id"));
			return setSuccessModelMap(modelMap);
		}
		throw new ParameterException("用户名或密码错误");
	}

	@ResponseBody
	@RequestMapping("/logout")
	public ModelMap logout(ModelMap modelMap, HttpServletRequest request) {
		RedisUtil.hdel(Constants.CURRENT_USER, request.getSession().getId());
		return setSuccessModelMap(modelMap);
	}
}