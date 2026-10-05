package com.personnel.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 当前阶段的通用内存 Repository 基础实现。 */
public abstract class InMemoryRepository<T> implements Repository<T, String> {
  protected final Map<String, T> data = new ConcurrentHashMap<>();
  @Override public T findById(String id) { return data.get(id); }
}
