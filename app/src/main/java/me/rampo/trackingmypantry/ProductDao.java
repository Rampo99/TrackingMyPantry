package me.rampo.trackingmypantry;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.List;
import java.util.Set;

@Dao
public interface ProductDao {
@Query("SELECT * FROM product")
List<Product> get();
@Insert()
void insert(Product... product);
@Delete
void delete(Product product);
@Query("SELECT * FROM product WHERE category IN(:filters)")
List<Product> getByFilters(Set<String> filters);
@Query("SELECT COUNT(category) FROM product GROUP BY category HAVING COUNT(category)<10 AND category = :category")
int getByCategory(String category);
@Query("SELECT COUNT(*) FROM product WHERE date IS NOT NULL AND date < :date")
int getByDate(String date);
@Query("SELECT * FROM product WHERE id = :productId")
Product getById(String productId);
@Query("UPDATE product SET place = :place WHERE id = :productId")
void addPlace(String productId, String place);
@Query("UPDATE product SET category = :category WHERE id = :id")
void addCategory(String id, String category);
@Query("UPDATE product SET date = :date, dateoutput = :date WHERE id = :id")
void addDate(String id, String date);
@Query("UPDATE product SET quantity = :q WHERE id = :id")
void updateProduct(String id,int q);
@Query("SELECT * from product where name LIKE :name AND category IN(:filters)")
List<Product> getByNameandFilter(String name, Set<String> filters);
@Query("SELECT * from product where name LIKE :name")
List<Product> getByName(String name);

}
