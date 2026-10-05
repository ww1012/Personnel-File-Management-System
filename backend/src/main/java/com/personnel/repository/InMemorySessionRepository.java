package com.personnel.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySessionRepository implements SessionRepository {
  private final Map<String, String> sessions = new ConcurrentHashMap<>();
  @Override public void save(String token, String username) { sessions.put(token, username); }
  @Override public String findUsername(String token) { return sessions.get(token); }
  @Override public void delete(String token) { sessions.remove(token); }
  @Override public void deleteByUsername(String username) { sessions.entrySet().removeIf(entry -> entry.getValue().equals(username)); }
}
