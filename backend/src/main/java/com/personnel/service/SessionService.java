package com.personnel.service;

import java.util.UUID;
import com.personnel.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService implements ServiceLayer {
  private final SessionRepository repository;
  public SessionService(SessionRepository repository) { this.repository = repository; }
  public String issue(String username) { String token = UUID.randomUUID().toString(); repository.save(token, username); return token; }
  public String usernameOf(String token) { return repository.findUsername(token); }
  public void revoke(String token) { repository.delete(token); }
  public void revokeAll(String username) { repository.deleteByUsername(username); }
}
