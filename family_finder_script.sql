USE [DB_9E3E00_myphonelocations]
GO
/****** Object:  UserDefinedFunction [dbo].[PostTimeLeft]    Script Date: 9/15/2016 8:32:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
 
create FUNCTION [dbo].[PostTimeLeft]
(
@datePost datetime
)
RETURNS nvarchar(50)
AS
BEGIN
-- Declare the return variable here
	DECLARE @DateMessage nvarchar(50)

declare @day int 
declare @hour int
set @day=(SELECT DATEDIFF(day, @datePost,getdate())) 
if(@day>1)
begin
set @DateMessage=(SELECT CONVERT(varchar, @datePost, 101) )
end
else --if lease than on day send message with hours =========
begin
set @hour =(SELECT DATEDIFF(hour, @datePost,getdate())) -- get hours
if(@hour>1) 
begin
if(@hour>23)
set @DateMessage = '1'  +  N' Day left';
else
set @DateMessage=  convert(varchar,@hour)  +  N' Hours left ';
end
else
begin --if lease than on hour send message with minute =========
declare @minute int
 set  @minute=(SELECT DATEDIFF(minute, @datePost,getdate())) -- get minute
if(@minute>0)
begin
if(@minute>59)
set @DateMessage= '1'  +  N' Hour left';
else
set @DateMessage=   convert(varchar,@minute)  +  N' Minutes left';

end
else
begin
declare @second int 
set @second =(SELECT DATEDIFF(second, @datePost,getdate())) -- get second
if(@second>0)
set @DateMessage=   convert(varchar,@second)  +  N' Seconds left ';
else
set @DateMessage=N'Now' 

end

end

end--===========================================
	RETURN @DateMessage

END


GO
/****** Object:  Table [dbo].[AdminPhones]    Script Date: 9/15/2016 8:32:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[AdminPhones](
	[UserID] [int] NULL,
	[PhoneMac] [varchar](50) NULL,
	[DateRegister] [datetime] NULL,
	[PhoneName] [varchar](50) NULL,
	[PhoneUID] [uniqueidentifier] NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Admins]    Script Date: 9/15/2016 8:32:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Admins](
	[UserID] [int] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
	[Password] [varchar](50) NULL,
	[DateRegister] [datetime] NULL,
	[EmailAdrress] [varchar](50) NULL,
	[UserUID] [uniqueidentifier] NULL,
 CONSTRAINT [PK_Admins] PRIMARY KEY CLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Tracking]    Script Date: 9/15/2016 8:32:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Tracking](
	[Latitude] [float] NULL,
	[longitude] [float] NULL,
	[DateRecord] [datetime] NULL,
	[BatteryLevel] [int] NULL,
	[PhoneUID] [uniqueidentifier] NULL
) ON [PRIMARY]

GO
/****** Object:  View [dbo].[UserUpdateLocation]    Script Date: 9/15/2016 8:32:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[UserUpdateLocation]
AS
SELECT        dbo.AdminPhones.PhoneUID, dbo.AdminPhones.PhoneName, dbo.Tracking.BatteryLevel, dbo.Tracking.Latitude, dbo.Tracking.longitude, dbo.Tracking.DateRecord, dbo.AdminPhones.UserID
FROM            dbo.AdminPhones INNER JOIN
                         dbo.Tracking ON dbo.AdminPhones.PhoneUID = dbo.Tracking.PhoneUID

GO
/****** Object:  View [dbo].[UserView]    Script Date: 9/15/2016 8:32:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[UserView]
AS
SELECT DISTINCT dbo.Admins.EmailAdrress, dbo.Admins.Password, dbo.AdminPhones.PhoneNumber, dbo.Tracking.Latitude, dbo.Tracking.longitude, dbo.Tracking.DateRecord, dbo.AccountType.AccountTypeName
FROM  dbo.Admins INNER JOIN
         dbo.AdminPhones ON dbo.Admins.UserID = dbo.AdminPhones.UserID INNER JOIN
         dbo.Tracking ON dbo.AdminPhones.PhoneID = dbo.Tracking.PhoneID INNER JOIN
         dbo.AccountType ON dbo.Admins.AccountType = dbo.AccountType.AccountTypeID

GO
ALTER TABLE [dbo].[AdminPhones] ADD  CONSTRAINT [DF_AdminPhones_DateRegister]  DEFAULT (getdate()) FOR [DateRegister]
GO
ALTER TABLE [dbo].[AdminPhones] ADD  CONSTRAINT [DF_AdminPhones_PhoneUID]  DEFAULT (newid()) FOR [PhoneUID]
GO
ALTER TABLE [dbo].[Admins] ADD  CONSTRAINT [DF_Admins_DateRegister]  DEFAULT (getdate()) FOR [DateRegister]
GO
ALTER TABLE [dbo].[Admins] ADD  CONSTRAINT [DF_Admins_UserUID]  DEFAULT (newid()) FOR [UserUID]
GO
ALTER TABLE [dbo].[Tracking] ADD  CONSTRAINT [DF_Tracking_DateRecord]  DEFAULT (getdate()) FOR [DateRecord]
GO
ALTER TABLE [dbo].[Tracking] ADD  CONSTRAINT [DF_Tracking_BatteryLevel]  DEFAULT ((0)) FOR [BatteryLevel]
GO
ALTER TABLE [dbo].[AdminPhones]  WITH CHECK ADD  CONSTRAINT [FK_AdminPhones_Admins] FOREIGN KEY([UserID])
REFERENCES [dbo].[Admins] ([UserID])
GO
ALTER TABLE [dbo].[AdminPhones] CHECK CONSTRAINT [FK_AdminPhones_Admins]
GO
/****** Object:  StoredProcedure [dbo].[GetUpdateLocation]    Script Date: 9/15/2016 8:32:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Script for SelectTopNRows command from SSMS  ******/
CREATE procedure [dbo].[GetUpdateLocation]( @UserID int)
as
SELECT   [PhoneUID]
      ,[PhoneName]
      ,[BatteryLevel]
      ,[Latitude]
      ,[longitude]
      ,dbo.PostTimeLeft([DateRecord]) as DateRecord
  FROM  [UserUpdateLocation] as MainUSerLocation with (nolock)
  where UserID=@UserID and 
   [DateRecord] =(select max ([DateRecord]) from [UserUpdateLocation]
  where PhoneUID=MainUSerLocation.PhoneUID and  UserID=@UserID )
GO
/****** Object:  StoredProcedure [dbo].[TrackingInsert]    Script Date: 9/15/2016 8:32:33 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Script for SelectTopNRows command from SSMS  ******/
CREATE procedure [dbo].[TrackingInsert]   ( @Latitude float,@longitude float,@PhoneUID uniqueidentifier,@BatteryLevel int)
as

insert into  Tracking([Latitude]
      ,[longitude]
      ,[PhoneUID],BatteryLevel) values (@Latitude,@longitude,@PhoneUID,@BatteryLevel);


GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "AdminPhones"
            Begin Extent = 
               Top = 7
               Left = 50
               Bottom = 206
               Right = 245
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "Tracking"
            Begin Extent = 
               Top = 8
               Left = 305
               Bottom = 188
               Right = 502
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'UserUpdateLocation'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'UserUpdateLocation'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "Admins"
            Begin Extent = 
               Top = 12
               Left = 76
               Bottom = 460
               Right = 396
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "AdminPhones"
            Begin Extent = 
               Top = 12
               Left = 427
               Bottom = 400
               Right = 723
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "Tracking"
            Begin Extent = 
               Top = 12
               Left = 778
               Bottom = 338
               Right = 1097
            End
            DisplayFlags = 280
            TopColumn = 0
         End
         Begin Table = "AccountType"
            Begin Extent = 
               Top = 306
               Left = 1317
               Bottom = 485
               Right = 1636
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'UserView'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'UserView'
GO
