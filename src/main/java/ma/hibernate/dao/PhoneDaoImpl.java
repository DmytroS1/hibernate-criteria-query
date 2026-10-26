package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.persist(phone);
            transaction.commit();

            return phone;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw new RuntimeException("Can't add new phone: " + phone, e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = factory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
        Root<Phone> root = query.from(Phone.class);

        List<Predicate> restrictions = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            CriteriaBuilder.In<Object> objectIn = cb.in(root.get(entry.getKey()));

            for (String value : entry.getValue()) {
                objectIn.value(value);
            }

            restrictions.add(objectIn);
        }

        query.where(cb.and(restrictions.toArray(new Predicate[0])));

        return session.createQuery(query).getResultList();
    }
}
