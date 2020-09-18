package ru.ku.yfrsmartweight.ServerConnection;

import java.io.Serializable;

import androidx.annotation.NonNull;

/*
    Класс симулирует структуры.
 */

public class ObjectStructures {

    // Объект, характеризующий одно блюдо
    static public class DishParams {
        public String dishName;
        public int mass;
        public int idImage;

        public DishParams(String dishName, int mass, int idImage) {
            this.dishName = dishName;
            this.mass = mass;
            this.idImage = idImage;
        }

        @NonNull
        @Override
        public String toString() {
            return dishName + " " + mass + " " + idImage;
        }
    }

    // Объект, характеризующий повара
    static public class CookParams {

        public String fullName;
        public String department_name;

        public CookParams(String fullName, String department_name) {
            this.fullName = fullName;
            this.department_name = department_name;
        }

    }
}
