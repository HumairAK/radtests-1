package com.redhat.xpaas.zeppelin;

import com.redhat.xpaas.zeppelin.entity.CodeCell;

public interface ZeppelinAPI {

  void loadProject(String projectName);

  CodeCell getNthCodeCell(int n);

  void webDriverCleanup();
}
