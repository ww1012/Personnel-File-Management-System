package com.personnel.repository;

/** 会话数据访问接口；当前实现为内存，后续可替换为 Redis 或数据库。 */
public interface SessionRepository {
  void save(String token, String username);
  String findUsername(String token);
  void delete(String token);
  void deleteByUsername(String username);
}
