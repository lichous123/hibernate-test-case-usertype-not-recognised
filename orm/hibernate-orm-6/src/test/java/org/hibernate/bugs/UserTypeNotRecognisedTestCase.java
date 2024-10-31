package org.hibernate.bugs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

class UserTypeNotRecognisedTestCase {

    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @AfterEach
    void destroy() {
        entityManagerFactory.close();
    }

    /**
     *
     */
    @Test
    void customUserTypeWithTypeContributorRegistrationTest() {
        final var em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        final var data = new CustomData("whatever", 1L);
        // creating some data, flushing and clearing context
        em.merge(new SomeEntity(new CustomData[]{data}));
        em.flush();
        em.clear();

        // getting the data
        final var query = em.createQuery("select s from SomeEntity s where id is not null", SomeEntity.class);
        final var resultList = query.getResultList();

        // single result should be present
        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(1, resultList.size());

        // the entity shouldn't be null
        final var entity = resultList.get(0);
        Assertions.assertNotNull(entity);

        // custom data array shouldn't be null and there should be single object present
        final var customData = entity.getCustomData();
        Assertions.assertNotNull(customData);
        Assertions.assertEquals(1, customData.length);

        // custom data object shouldn't be null and all fields should be set with correct values
        final var singleCustomData = customData[0];
        Assertions.assertNotNull(singleCustomData);
        Assertions.assertEquals(data.getText(), singleCustomData.getText());
        Assertions.assertEquals(data.getNumber(), singleCustomData.getNumber());

        // Do stuff...
        em.getTransaction().commit();
        em.close();
    }
}
