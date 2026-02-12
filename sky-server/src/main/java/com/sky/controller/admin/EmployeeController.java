package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;

import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "和员工操作相关的controller")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工退出登录")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工的功能
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value="员工新增")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        System.out.println(Thread.currentThread().getId());

        employeeService.save(employeeDTO);
        return Result.success(); //返回success
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询员工的信息")
    public Result<PageResult> queryEmployee(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工查询: {}", employeePageQueryDTO);
        PageResult query_result = employeeService.queryPage(employeePageQueryDTO);
        return Result.success(query_result);
    }

    @PostMapping("/status/{status}")
    @ApiOperation(value = "更新员工状态")
    public Result enableEmployee(@PathVariable("status") int enabled, long id){
        log.info("启用/禁用员工: {}, {}", enabled, id);
        employeeService.enableEmployee(enabled, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询得到一个员工的详细信息")
    public Result<Employee> queryEMployee(@PathVariable("id") long id){
        log.info("查询员工信息: {}", id);
        Employee employee_info = employeeService.queryEmployee(id);
        return Result.success(employee_info);
    }

    @PutMapping
    @ApiOperation(value = "修改一个员工的信息")
    public Result updataEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工信息: {}", employeeDTO);
        employeeService.updataEmployee(employeeDTO);
        return Result.success();
    }
}









