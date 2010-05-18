/**
 * Copyright (c) 2008-2010 Alexander Maslov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yaml.snakeyaml.generics;

import java.util.Map;

public class ObjectValuesWithParam<T, S> {

    private Object object;
    private Map<T, Map<S, Object>> values;
    private T[] possible;

    public Object getObject() {
        return object;
    }
    
    public void setObject(Object object) {
        this.object = object;
    }
    
    public void setValues(Map<T, Map<S, Object>> values) {
        this.values = values;
    }

    public Map<T, Map<S, Object>> getValues() {
        return values;
    }

    public void setPossible(T[] possible) {
        this.possible = possible;
    }
    
    public T[] getPossible() {
        return possible;
    }
    
}
