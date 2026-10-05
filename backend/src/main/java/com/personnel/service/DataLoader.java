package com.personnel.service;

import com.personnel.model.*;
import com.personnel.repository.*;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** mysql profile 下：库为空时写入与内存版一致的种子数据（部门-岗位-员工三级关联）。 */
@Component
@Profile("mysql")
public class DataLoader {
  private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private final AccountRepository accountRepository;
  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final PositionRepository positionRepository;
  private final ChangeRepository changeRepository;

  public DataLoader(AccountRepository ar, EmployeeRepository er, DepartmentRepository dr, PositionRepository pr, ChangeRepository cr) {
    this.accountRepository = ar; this.employeeRepository = er; this.departmentRepository = dr;
    this.positionRepository = pr; this.changeRepository = cr;
  }

  @PostConstruct
  public void load() {
    if (departmentRepository.count() > 0) return;
    BCryptPasswordEncoder pw = new BCryptPasswordEncoder();
    // 部门
    departmentRepository.save(new Department("d-tech", "技术部", "负责系统研发"));
    departmentRepository.save(new Department("d-hr", "人力资源部", "负责组织与人员管理"));
    departmentRepository.save(new Department("d-fin", "财务部", "负责财务与资金管理"));
    departmentRepository.save(new Department("d-mkt", "市场部", "负责品牌与市场推广"));
    departmentRepository.save(new Department("d-sales", "销售部", "负责客户拓展与销售"));
    // 岗位（归属部门）
    positionRepository.save(new Position("p-engineer", "软件工程师", "", "d-tech"));
    positionRepository.save(new Position("p-qa", "测试工程师", "", "d-tech"));
    positionRepository.save(new Position("p-tech-lead", "技术经理", "", "d-tech"));
    positionRepository.save(new Position("p-specialist", "人事专员", "", "d-hr"));
    positionRepository.save(new Position("p-recruiter", "招聘主管", "", "d-hr"));
    positionRepository.save(new Position("p-accountant", "会计", "", "d-fin"));
    positionRepository.save(new Position("p-fin-lead", "财务经理", "", "d-fin"));
    positionRepository.save(new Position("p-marketer", "市场专员", "", "d-mkt"));
    positionRepository.save(new Position("p-mkt-lead", "市场经理", "", "d-mkt"));
    positionRepository.save(new Position("p-sales-rep", "销售代表", "", "d-sales"));
    positionRepository.save(new Position("p-sales-lead", "销售经理", "", "d-sales"));
    // 员工
    Object[][] emp = {
      {"e-001","E2024001","刘洋","18840241672","liuyang@example.com","北京市朝阳区","本科","d-tech","p-engineer","2024-03-11"},
      {"e-002","E2024002","张敏","13912345678","zhangmin@example.com","上海市浦东新区","硕士","d-hr","p-specialist","2024-05-20"},
      {"e-003","E2024003","陈伟","13700001111","chenwei@example.com","北京市海淀区","本科","d-tech","p-qa","2024-06-01"},
      {"e-004","E2024004","赵磊","13700002222","zhaolei@example.com","北京市西城区","硕士","d-tech","p-tech-lead","2023-11-15"},
      {"e-005","E2024005","王芳","13700003333","wangfang@example.com","上海市静安区","本科","d-hr","p-recruiter","2024-07-10"},
      {"e-006","E2024006","李娜","13700004444","lina@example.com","广州市天河区","本科","d-fin","p-accountant","2024-02-18"},
      {"e-007","E2024007","孙强","13700005555","sunqiang@example.com","深圳市南山区","硕士","d-fin","p-fin-lead","2022-09-01"},
      {"e-008","E2024008","周婷","13700006666","zhouting@example.com","成都市武侯区","本科","d-mkt","p-marketer","2024-08-05"},
      {"e-009","E2024009","吴昊","13700007777","wuhao@example.com","武汉市江汉区","本科","d-sales","p-sales-rep","2024-04-22"},
      {"e-010","E2024010","郑爽","13700008888","zhengshuang@example.com","杭州市西湖区","硕士","d-sales","p-sales-lead","2023-12-03"},
    };
    List<Employee> saved = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();
    for (Object[] r : emp) {
      Employee e = new Employee((String) r[0], (String) r[1], (String) r[2], (String) r[3], (String) r[4], (String) r[5], (String) r[6], (String) r[7], (String) r[8], (String) r[9], "ACTIVE", now, now);
      employeeRepository.save(e); saved.add(e);
    }
    // 账号（admin + 两名员工）
    accountRepository.save(new Account("admin", pw.encode("Admin@123"), "ADMIN", null, true));
    accountRepository.save(new Account("liuyang", pw.encode("Employee@123"), "EMPLOYEE", "e-001", true));
    accountRepository.save(new Account("zhangmin", pw.encode("Employee@123"), "EMPLOYEE", "e-002", true));
    // 入职变动记录（时间精确到秒，按序错开以便列表按时间倒序展示）
    int ci = 0;
    for (Employee e : saved) { changeRepository.save(new Change("c-" + e.getId(), "ONBOARD", e.getId(), e.getName(), e.getEmployeeNo(), now.minusMinutes(ci * 3).format(TS_FMT), "", "", e.getDepartmentId(), e.getPositionId(), "新增员工入职", "admin")); ci++; }
  }
}
