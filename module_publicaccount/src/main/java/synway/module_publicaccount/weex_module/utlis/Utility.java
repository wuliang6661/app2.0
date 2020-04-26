/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package synway.module_publicaccount.weex_module.utlis;

import android.util.Pair;

/**
 * Created by sospartan on 28/03/2017.
 */

public class Utility {
  private static final int[] ID_STUBS = {
  };

  private static final int COUNT = ID_STUBS.length;
  private static int sCurrentIDIndex = 0;

  public static Pair<String,Integer> nextID(){
    Pair<String,Integer> result = new Pair<>("weex_id_"+(COUNT-1-sCurrentIDIndex),ID_STUBS[sCurrentIDIndex]);
    sCurrentIDIndex = (sCurrentIDIndex+1)%COUNT;
    return result;
  }
}
