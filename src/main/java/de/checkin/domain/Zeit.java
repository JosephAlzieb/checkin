package de.checkin.domain;

public enum Zeit {
  NULL(0), FUENFZEHN(15), DREISSIG(30), FUENFUNDVIERZIG(45);

  private final int minute;

  Zeit(int minute) {
    this.minute = minute;
  }

  public int getMinutes() {
    return minute;
  }
}