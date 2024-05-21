package com.t1.tripfy.mapper.pack;

import org.apache.ibatis.annotations.Mapper;
import com.t1.tripfy.domain.dto.pack.PackageFileDTO;

@Mapper
public interface PackageFileMapper {
	//C
	int insertFile(PackageFileDTO file);
	//R
	PackageFileDTO getFileBySystemname(String systemname);
	//D
	int deleteFileBySystemname(String systemname);
	int deleteFilesByBoardnum(long boardnum);
}



