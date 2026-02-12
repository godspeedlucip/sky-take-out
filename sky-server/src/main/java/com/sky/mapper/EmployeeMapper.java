package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into employee (name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user)" +
            "values " +
            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Employee employee);

//    /**
//     * 根据给定的用户名来获取信息
//     * @param name
//     * @return
//     */
//    @Select("select * from employee where username=#{name}")
//    List<Employee> query_single_employee(String name);
//
//    /**
//     * 查询所有员工的信息
//     * @param start, pageSize
//     * @param pageSize
//     * @return
//     */
//    @Select("select * from employee " +
//            "order by id ASC " +
//            "limit #{start}, #{pageSize}")
//    List<Employee> query_all_employee(int start, int pageSize);

    /**
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用 or 禁用员工
     * @param employee
     */
//    @Update("update employee set status = #{i} where id = #{id}")
    @AutoFill(value = OperationType.UPDATE)
    void updateEmployee(Employee employee);

    /**
     * 查询得到单个员工的信息
     * @param id
     * @return
     */
    @Select("select username, name, phone ,sex, id_number from employee where id = #{id}")
    Employee queryEmployee(long id);


    /**
     * 启用 or 禁用员工
     * @param employee
     */
//    @Update("update employee set status = #{i} where id = #{id}")
    @AutoFill(value = OperationType.UPDATE)
    void updateEmployeeByUsername(Employee employee);

}
