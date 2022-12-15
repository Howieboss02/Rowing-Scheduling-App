package nl.tudelft.sem.template.database;


import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import nl.tudelft.sem.template.shared.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestUserRepository implements UserRepository {

  public final List<User> users = new ArrayList<>();
  public final List<String> used = new ArrayList<>();

  /**
   * Adds a called method to the used list
   * @param s the name of the method
   */
  public void call(String s){
    used.add(s);
  }


  @Override
  public List<User> findAll() {
    call("findAll");
    return users;
  }

  @Override
  public List<User> findAll(Sort sort) {
    return null;
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return null;
  }

  @Override
  public List<User> findAllById(Iterable<Long> longs) {
    return null;
  }

  @Override
  public long count() {
    call("count");
    return users.size();
  }

  @Override
  public void deleteById(Long aLong) {
    call("deleteById");
    users.remove(aLong);
  }

  @Override
  public void delete(User entity) {
    call("delete");
    users.remove(entity);
  }

  @Override
  public void deleteAll(Iterable<? extends User> entities) {

  }

  @Override
  public void deleteAll() {

  }

  @Override
  public <S extends User> S save(S entity) {
    call("save");
    entity.setId((long) users.size());
    users.add(entity);
    return entity;
  }

  @Override
  public <S extends User> List<S> saveAll(Iterable<S> entities) {
    return null;
  }

  @Override
  public Optional<User> findById(Long aLong) {
    return Optional.empty();
  }

  @Override
  public boolean existsById(Long aLong) {
    return false;
  }

  @Override
  public void flush() {

  }

  @Override
  public <S extends User> S saveAndFlush(S entity) {
    return null;
  }



  @Override
  public void deleteInBatch(Iterable<User> entities) {

  }


  @Override
  public void deleteAllInBatch() {

  }

  @Override
  public User getOne(Long aLong) {
    return null;
  }


  @Override
  public <S extends User> Optional<S> findOne(Example<S> example) {
    return Optional.empty();
  }

  @Override
  public <S extends User> List<S> findAll(Example<S> example) {
    return null;
  }

  @Override
  public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
    return null;
  }

  @Override
  public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
    return null;
  }

  @Override
  public <S extends User> long count(Example<S> example) {
    return 0;
  }

  @Override
  public <S extends User> boolean exists(Example<S> example) {
    return false;
  }

}
