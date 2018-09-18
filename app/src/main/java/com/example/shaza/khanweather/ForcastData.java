package com.example.shaza.khanweather;

import java.util.List;

class ForcastData    {
        private List<Lists> list;


        public ForcastData(List<Lists> list) {
            this.list = list;
        }

        public List<Lists> getList() {
            return list;
        }

        public void setList(List<Lists> list) {
            this.list = list;
        }
}
