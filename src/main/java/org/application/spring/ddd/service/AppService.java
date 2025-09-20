package org.application.spring.ddd.service;

import org.application.spring.ddd.model.entity.AppEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AppService<E extends AppEntity, ID, R extends JpaRepository<E, ID>> implements JpaRepository<E, ID> {

    protected final R repository;

    public AppService(R repository) {
        this.repository = repository;
    }

/*    public R getRepository() {
        return repository;
    }*/

    @Transactional(transactionManager = "appTM")
    public <S extends E> S save(S entity) {
        S s = this.repository.save(entity);
        //this.repository.flush();
        return s;
    }

    @Transactional(transactionManager = "appTM")
    public <S extends E> List<S> saveAll(Iterable<S> entities) {
        List<S> list = this.repository.saveAll(entities);
        //this.repository.flush();
        return list;
    }

    public Optional<E> findById(ID id) {
        return repository.findById(id);
    }

    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    public List<E> findAll() {
        return repository.findAll();
    }

    public List<E> findAllById(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    public long count() {
        return repository.count();
    }

    @Transactional(transactionManager = "appTM")
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Transactional(transactionManager = "appTM")
    public void delete(E entity) {
        repository.delete(entity);
    }

    @Transactional(transactionManager = "appTM")
    public void deleteAll(Iterable<? extends E> entities) {
        repository.deleteAll(entities);
    }

    @Transactional(transactionManager = "appTM")
    public void deleteAll() {
        repository.deleteAll();
    }

    public List<E> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public Page<E> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public <S extends E> Optional<S> findOne(Example<S> example) {
        return repository.findOne(example);
    }

    public <S extends E> List<S> findAll(Example<S> example) {
        return repository.findAll(example);
    }

    public <S extends E> List<S> findAll(Example<S> example, Sort sort) {
        return repository.findAll(example, sort);
    }

    public <S extends E> Page<S> findAll(Example<S> example, Pageable pageable) {
        return repository.findAll(example, pageable);
    }

    public <S extends E> long count(Example<S> example) {
        return repository.count(example);
    }

    public <S extends E> boolean exists(Example<S> example) {
        return repository.exists(example);
    }

    public <S extends E, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return repository.findBy(example, queryFunction);
    }

    public void flush() {
        repository.flush();
    }

    public <S extends E> S saveAndFlush(S entity) {
        return repository.saveAndFlush(entity);
    }

    public <S extends E> List<S> saveAllAndFlush(Iterable<S> entities) {
        return repository.saveAllAndFlush(entities);
    }

    @Deprecated
    @Transactional(transactionManager = "appTM")
    public void deleteInBatch(Iterable<E> entities) {
        repository.deleteInBatch(entities);
    }

    @Transactional(transactionManager = "appTM")
    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }

    @Transactional(transactionManager = "appTM")
    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        repository.deleteAllByIdInBatch(ids);
    }

    @Transactional(transactionManager = "appTM")
    public void deleteAllInBatch(Iterable<E> entities) {
        repository.deleteAllInBatch(entities);
    }

    @Deprecated
    public E getOne(ID id) {
        return repository.getOne(id);
    }

    public E getById(ID id) {
        return repository.getById(id);
    }

    public E getReferenceById(ID id) {
        return repository.getReferenceById(id);
    }

    public void deleteAllById(Iterable<? extends ID> iterable) {
        repository.deleteAllById(iterable);
    }


}
