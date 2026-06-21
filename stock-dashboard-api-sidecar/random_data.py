import psycopg
import json
import random

# 🔑 Update these connection details for your local database
CONN_INFO = "dbname=your_db user=postgres password=your_password host=localhost port=5432"

def populate_db():
    with psycopg.connect(CONN_INFO) as conn:
        with conn.cursor() as cur:
            # Clear old data and create the table
            cur.execute("""
                DROP TABLE IF EXISTS items;
                CREATE TABLE items (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100),
                    details JSONB
                );
            """)
            
            print("Populating 10,000 rows...")
            categories = ['electronics', 'clothing', 'home', 'sports']
            colors = ['red', 'blue', 'green', 'black', 'white']
            tags_pool = ['sale', 'new', 'clearance', 'top-rated', 'limited']
            
            # Efficient bulk insertion using the COPY protocol
            with cur.copy("COPY items (name, details) FROM STDIN") as copy:
                for i in range(10000):
                    data = {
                        "category": random.choice(categories),
                        "price": round(random.uniform(10.0, 1000.0), 2),
                        "instock": random.choice([True, False]),
                        "specs": {
                            "color": random.choice(colors),
                            "weight_kg": round(random.uniform(0.1, 15.0), 1)
                        },
                        "tags": list(set(random.choices(tags_pool, k=2)))
                    }
                    copy.write_row([f"Product_{i}", json.dumps(data)])
                    
    print("Database populated successfully!")

if __name__ == "__main__":
    populate_db()