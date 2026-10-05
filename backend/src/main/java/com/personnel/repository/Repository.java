package com.personnel.repository;

/** 数据访问隔离标记；MySQL 接入时以同名接口替换内存实现。 */
public interface Repository<T, ID> {
  T findById(ID id);
  void save(T entity);
}
