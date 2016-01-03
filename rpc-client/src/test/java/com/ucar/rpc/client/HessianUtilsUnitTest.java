package com.ucar.rpc.client;

import com.ucar.rpc.common.utils.Hessian1Utils;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by zhangyong on 16/1/3.
 */
public class HessianUtilsUnitTest {

    @Test
    public void test() throws IOException {
        Object[] params = new Object[]{
                "hello", 24
        };
        byte[] buf = Hessian1Utils.encodeObject(params);
        Object[] newPrams = Hessian1Utils.decodeObject(buf, 2);
        System.out.println(newPrams);
    }

}
