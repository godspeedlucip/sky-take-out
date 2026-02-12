package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.sky.context.BaseContext;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传送过来的明文密码进行md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes()); // spring boot默认自带的md5加密器
        if (!password.equalsIgnoreCase(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增用户
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        System.out.println(Thread.currentThread().getId());
        // 前后端传输数据用的是EmployeeLoginDTO, 但是写入到数据中用一个employee对象是最好
        Employee employee = new Employee();

        // 设置默认密码，并进行MD5加密 (假设默认密码是 123456)
        // 这里的 PasswordConstant.DEFAULT_PASSWORD 通常在常量类中定义为 "123456"
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 把对应的值传过来
        BeanUtils.copyProperties(employeeDTO, employee); //保证employeeLoginDTO的属性都在employee有

        //设置statue
        employee.setStatus(StatusConstant.ENABLE); //用常量, 不要写死

        //设置创建和更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //设置创建人和修改人
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        //调用mapper层数据
        employeeMapper.insert(employee);
    }


//    @Override
//    public List<Employee> query(EmployeePageQueryDTO employeePageQueryDTO) {
//        String name = employeePageQueryDTO.getName();
//        int page = employeePageQueryDTO.getPage();
//        int pageSize = employeePageQueryDTO.getPageSize();
//
//        List<Employee> queryed_employees = null;
//        // 如果没有输入要查询的员工的名字
//        if(name == null || name.isEmpty()) {
//            int start_index = (page - 1) * pageSize;
//            queryed_employees = employeeMapper.query_all_employee(start_index, pageSize);
//        }
//        else{
//            queryed_employees = employeeMapper.query_single_employee(name);
//        }
//        return queryed_employees;
//    }

    @Override
    public PageResult queryPage(EmployeePageQueryDTO employeePageQueryDTO) {
        // 使用pageHelper插件
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        // 返回值必须是pageHelper插件要求的
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        long total = page.getTotal();
        List<Employee> result = page.getResult();

        return new PageResult(total, result);
    }

    /**
     * 禁用 or 启用 员工
     * @param enabled
     * @param id
     */
    public void enableEmployee(int enabled, long id){
        // sql语句打算写一个通用的修改语句，因此这里最好是给mapper传一个employee对象
        Employee employee = Employee.builder()
                .id(id)
                .status(enabled)
                .build();
        employeeMapper.updateEmployee(employee);
    }

    /**
     * 查询得到一个员工的详细信息
     * @param id
     */
    public Employee queryEmployee(long id){
        Employee employee = employeeMapper.queryEmployee(id);
        employee.setPassword("****");
        return employee;
    }


    /**
     * 修改员工的信息
     * @param employeeDTO
     */
    public void updataEmployee(EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.updateEmployeeByUsername(employee);
    }

}
