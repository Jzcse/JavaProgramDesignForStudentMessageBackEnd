package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {
    @Autowired
    TeacherRepository teacherRepository;

    /**
     * 获取老师信息列表
     * @param dataRequest
     * @return dataResponse
     */
    public DataResponse getTeacherList(@Valid DataRequest dataRequest) {
        return null;
    }

    /**
     * 删除老师
     * @param dataRequest
     * @return dataResponse
     */
    public DataResponse deleteTeacher(@Valid DataRequest dataRequest) {
        return null;
    }

    /**
     * 更改老师的个人信息
     * @param dataRequest
     * @return dataResponse
     */
    public DataResponse editTeacherInfo(@Valid DataRequest dataRequest) {
        return null;
    }

    /**
     * 获取一名老师的详细个人信息
     * @param dataRequest
     * @return dataResponse
     */
    public DataResponse getTeacherInfo(@Valid DataRequest dataRequest) {
        return null;
    }

    /**
     * 通过名字查找老师
     * @param dataRequest
     * @return
     */
    public DataResponse searchTeacherByName(@Valid DataRequest dataRequest) {
        return null;
    }
}
