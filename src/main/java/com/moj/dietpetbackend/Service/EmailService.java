package com.moj.dietpetbackend.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${EMAIL_USERNAME}")
    private String sender;

    public void sendVerifyEmail(String email, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            System.out.println("setting up to send email");
            String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                      <meta charset="UTF-8">
                      <title>2FA Code</title>
                    </head>
                    <body style="margin:0; padding:0; background-color:#f4f4f4; font-family:Arial, sans-serif;">
                      <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f4; padding:40px 0;">
                        <tr>
                          <td align="center">
                            <table width="420" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:12px; padding:32px; text-align:center;">
                              <tr>
                                <td style="font-size:28px; font-weight:bold; color:#222;">
                                  DietPet
                                </td>
                              </tr>
                              <tr>
                                <td style="padding-top:20px; font-size:22px; font-weight:bold; color:#222;">
                                  Verify your sign in
                                </td>
                              </tr>
                              <tr>
                                <td style="padding-top:12px; font-size:15px; color:#555; line-height:1.6;">
                                  Use the following verification code to continue:
                                </td>
                              </tr>
                              <tr>
                                <td style="padding-top:24px;">
                                  <div style="display:inline-block; padding:16px 28px; font-size:32px; font-weight:bold; letter-spacing:8px; color:#111; background:#f7f7f7; border-radius:10px;">
                                    %s
                                  </div>
                                </td>
                              </tr>
                              <tr>
                                <td style="padding-top:24px; font-size:14px; color:#666; line-height:1.6;">
                                  This code expires in 10 minutes.
                                </td>
                              </tr>
                              <tr>
                                <td style="padding-top:10px; font-size:13px; color:#888; line-height:1.6;">
                                  If you didn’t request this code, you can safely ignore this email.
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </body>
                    </html>
                    """.formatted(code);

            helper.setFrom(sender);
            helper.setTo(email);
            helper.setSubject("2FA Code");
            helper.setText(htmlContent, true);
            System.out.println("send email");
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    public void sendToChangePassword(String email, String code){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <title>Password Reset Code</title>
            </head>
            <body style="margin:0; padding:0; background-color:#f4f4f4; font-family:Arial, sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f4; padding:40px 0;">
                <tr>
                  <td align="center">
                    <table width="420" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:12px; padding:32px; text-align:center;">
                      <tr>
                        <td style="font-size:28px; font-weight:bold; color:#222;">
                          DietPet
                        </td>
                      </tr>
                      <tr>
                        <td style="padding-top:20px; font-size:22px; font-weight:bold; color:#222;">
                          Reset your password
                        </td>
                      </tr>
                      <tr>
                        <td style="padding-top:12px; font-size:15px; color:#555; line-height:1.6;">
                          Use the following verification code to reset your password:
                        </td>
                      </tr>
                      <tr>
                        <td style="padding-top:24px;">
                          <div style="display:inline-block; padding:16px 28px; font-size:32px; font-weight:bold; letter-spacing:8px; color:#111; background:#f7f7f7; border-radius:10px;">
                            %s
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding-top:24px; font-size:14px; color:#666; line-height:1.6;">
                          This code expires in 10 minutes.
                        </td>
                      </tr>
                      <tr>
                        <td style="padding-top:10px; font-size:13px; color:#888; line-height:1.6;">
                          If you didn’t request a password reset, you can safely ignore this email.
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(code);

            helper.setFrom(sender);
            helper.setTo(email);
            helper.setSubject("2FA Code");
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}