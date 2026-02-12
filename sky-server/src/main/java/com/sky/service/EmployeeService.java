package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

import java.util.List;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增用户
     * @param EmployeeDTO
     */
    void save(EmployeeDTO EmployeeDTO);

    /**
     * 查询员工的信息
     * @param employeePageQueryDTO
     */
//    List<Employee> query(EmployeePageQueryDTO employeePageQueryDTO);

    PageResult queryPage(EmployeePageQueryDTO employeePageQueryDTO);

    public void enableEmployee(int enabled, long id);

    /**
     * 查询得到一个员工的详细信息
     * @param id
     */
    Employee queryEmployee(long id);

    /**
     * 修改员工的信息
     * @param employeeDTO
     */
    void updataEmployee(EmployeeDTO employeeDTO);
}
